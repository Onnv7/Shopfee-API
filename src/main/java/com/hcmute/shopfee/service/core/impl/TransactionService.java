package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.constant.ShopfeeConstant;
import com.hcmute.shopfee.entity.sql.database.payment.VNPayEntity;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.kafka.message.NewOrderMsgData;
import com.hcmute.shopfee.entity.sql.database.CoinHistoryEntity;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.kafka.publisher.UserNotificationKafkaPublisher;
import com.hcmute.shopfee.module.vnpay.VNPayConstant;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.payment.ZaloPayEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.TransactionStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.dto.common.vnpay.TransactionInfoQuery;
import com.hcmute.shopfee.dto.common.zalopay.GetOrderZaloPayResponse;
import com.hcmute.shopfee.dto.common.zalopay.RefundRequestDTO;
import com.hcmute.shopfee.repository.database.CoinHistoryRepository;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.service.common.VNPayService;
import com.hcmute.shopfee.service.common.ZaloPayService;
import com.hcmute.shopfee.service.core.ITransactionService;
import com.hcmute.shopfee.statemachine.OrderEvent;
import com.hcmute.shopfee.statemachine.OrderStateService;
import com.hcmute.shopfee.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.hcmute.shopfee.constant.ShopfeeConstant.REFUND_COIN_ORDER;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;
    private final OrderBillRepository orderBillRepository;
    private final VNPayService vnPayService;
    private final ZaloPayService zaloPayService;
    private final CoinHistoryRepository coinHistoryRepository;
    private final UserNotificationKafkaPublisher userNotificationKafkaPublisher;
    private final OrderStateService orderStateService;

    @Transactional
    @Override
    public void updateTransaction(String id, HttpServletRequest request) {
        TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.TRANSACTION_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + id));

        boolean isSuccess = false;
        OrderBillEntity orderBill = transaction.getOrderBill();
        UserEntity user = orderBill.getUser();

        SecurityUtils.checkUserId(user.getId());

        // Goi den VNPay de lay thong tin
        if (transaction.getPaymentType() == PaymentType.VNPAY) {
            TransactionInfoQuery transInfo = vnPayService.getTransactionInfo(((VNPayEntity) transaction).getInvoiceCode(), ((VNPayEntity) transaction).getTimeCode(), request);
            if(!transInfo.getResponseCode().equals("00")) {
                return;
            }
            // nếu giao dịch vnpay thành công
            if (transInfo.getTransactionStatus().equals("00") && transInfo.getAmount() != null && transInfo.getAmount().equals(String.valueOf(orderBill.getTotalPayment() * 100))) {
                transaction.setStatus(TransactionStatus.PAID);
                transaction.setTotalPaid(Long.parseLong(transInfo.getAmount().toString()) / 100);
                isSuccess = true;
            }
            else if (transInfo.getTransactionStatus().equals("01")) {
                // giao dich chua xu ly xong -> co the retry
            }
            else {
                boolean rs = orderStateService.sendMonoEvent(orderBill.getId(), ShopfeeConstant.PAYMENT_FAILED_MSG, OrderEvent.PAYMENT_FAILED);
                if (!rs) {
                    throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY);
                }

                orderBillRepository.save(orderBill);
                transaction.setTotalPaid(0L);
                transaction.setStatus(TransactionStatus.FAILED);
            }
        } else if (transaction.getPaymentType() == PaymentType.ZALOPAY) {
            GetOrderZaloPayResponse transResult = zaloPayService.getOrderTransactionInformation(((ZaloPayEntity) transaction).getAppTransactionId());

            if (transResult.getReturnCode() == 1 && transResult.getAmount() == orderBill.getTotalPayment()) {
                isSuccess = true;
                transaction.setStatus(TransactionStatus.PAID);
                transaction.setTotalPaid((long) transResult.getAmount());
                ((ZaloPayEntity) transaction).setZalopayTransactionId(String.valueOf(transResult.getZpTransId()));
            } else if (transResult.getReturnCode() == 2) {
                boolean rs = orderStateService.sendMonoEvent(orderBill.getId(), ShopfeeConstant.PAYMENT_FAILED_MSG, OrderEvent.PAYMENT_FAILED);
                if (!rs) {
                    throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY);
                }

                ((ZaloPayEntity) transaction).setZalopayTransactionId(String.valueOf(transResult.getZpTransId()));
                transaction.setTotalPaid(0L);
                transaction.setStatus(TransactionStatus.FAILED);
                orderBillRepository.save(orderBill);
            } else if(transResult.getReturnCode() == 3) {
                // pending
                transaction.setTotalPaid(0L);
                transaction.setStatus(TransactionStatus.UNPAID);
            }
        }
        String orderType  = orderBill.getOrderType() == OrderType.ONSITE ? ShopfeeConstant.ONSITE_ORDER_TITLE_MSG : ShopfeeConstant.SHIPPING_ORDER_TITLE_MSG;
        if(isSuccess && transaction.getPaymentType() != PaymentType.CASHING) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("order_id", orderBill.getId());
            NewOrderMsgData notificationDto = new NewOrderMsgData(orderBill.getBranch().getId(), String.format(ShopfeeConstant.NEW_ORDER_MSG, orderType, orderBill.getId()), data);
            userNotificationKafkaPublisher.sendNotificationToBranch(notificationDto);
        }
        // Cập nhật kết quả từ vnpay vào database
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void refundOrder(OrderBillEntity orderBill, boolean refundCoin, boolean refundMoney) {
        TransactionEntity transaction = orderBill.getTransaction();
        if(transaction.getStatus() == TransactionStatus.REFUNDED) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY, "Order has been refunded");
        }

        if (refundCoin) {
            long coin = orderBill.getCoin();
            if (coin > 0) {
                UserEntity user = orderBill.getUser();
                CoinHistoryEntity coinHistory = CoinHistoryEntity.builder()
                        .coin(coin)
                        .actor(ActorType.AUTOMATIC)
                        .description(REFUND_COIN_ORDER)
                        .user(user)
                        .build();
                coinHistoryRepository.save(coinHistory);
                transaction.setStatus(TransactionStatus.REFUNDED);
            }
        }
        if(transaction.getStatus() != TransactionStatus.PAID) {
            return;
        }
        if(refundMoney) {
            try {
                boolean isRefunded = refundTransaction(null, transaction);
                if(isRefunded) {
                    transaction.setStatus(TransactionStatus.REFUNDED);
                } else {
                    throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.SERVER_ERROR, "The payment side service failed, please try again later");
                }
            } catch (IOException | URISyntaxException e) {
                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.SERVER_ERROR, "Refund error in the system");
            } catch (Exception e) {
                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.SERVER_ERROR, "Unexpected error");
            }
        }
        transactionRepository.save(transaction);
    }

    private boolean refundTransaction(HttpServletRequest request, TransactionEntity transaction ) throws IOException, URISyntaxException {
        if (transaction.getPaymentType() == PaymentType.CASHING) {
            return false;
        }
        if (transaction.getPaymentType() == PaymentType.VNPAY) {
            Map<String, Object> responseRefund = vnPayService.refundOrder(request, ((VNPayEntity) transaction).getTimeCode(), ((VNPayEntity) transaction).getInvoiceCode(), transaction.getTotalPaid());
            String responseCode = responseRefund.get(VNPayConstant.VNP_RESPONSE_CODE).toString();
            if (responseCode.equals("00")) {
                // refund thafnh coong
                return true;
            } else {
                // chuc nang refund ko hoat dong nen gia dinh la true luon
                return true;
            }
        } else if (transaction.getPaymentType() == PaymentType.ZALOPAY) {
            RefundRequestDTO refundRequestDTO = new RefundRequestDTO();
            ZaloPayEntity zaloPay = ((ZaloPayEntity) transaction);
            refundRequestDTO.setAmount(transaction.getTotalPaid());
            refundRequestDTO.setZpTransId(zaloPay.getZalopayTransactionId());
            refundRequestDTO.setDescription("");
            Map<String, Object> responseRefund = zaloPayService.sendRefund(refundRequestDTO);
            int returnCode = (int) responseRefund.getOrDefault("return_code", -1);
            if (returnCode == 1) {
                // refund thafnh coong
                return true;
            } else {
                return true;
            }
        }
        return false;
    }
}
