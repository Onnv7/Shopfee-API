package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.CoinHistoryEntity;
import com.hcmute.shopfee.module.vnpay.VNPayConstant;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.entity.sql.database.payment.ZaloPayEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.dto.common.vnpay.TransactionInfoQuery;
import com.hcmute.shopfee.dto.common.zalopay.GetOrderZaloPayResponse;
import com.hcmute.shopfee.dto.common.zalopay.RefundRequestDTO;
import com.hcmute.shopfee.repository.database.CoinHistoryRepository;
import com.hcmute.shopfee.repository.database.UserRepository;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.service.common.AuditorAwareService;
import com.hcmute.shopfee.service.common.VNPayService;
import com.hcmute.shopfee.service.common.ZaloPayService;
import com.hcmute.shopfee.service.core.IOrderService;
import com.hcmute.shopfee.service.core.ITransactionService;
import com.hcmute.shopfee.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static com.hcmute.shopfee.constant.ShopfeeConstant.REFUND_COIN_ORDER;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;
    private final IOrderService orderService;
    private final OrderBillRepository orderBillRepository;
    private final VNPayService vnPayService;
    private final ZaloPayService zaloPayService;
    private final AuditorAwareService auditorAwareService;
    private final CoinHistoryRepository coinHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void updateTransaction(String id, HttpServletRequest request) {
        TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ShopfeeException(ErrorConstant.NOT_FOUND, "Transaction with id " + id));

        OrderBillEntity orderBill = transaction.getOrderBill();
        UserEntity user = orderBill.getUser();

        SecurityUtils.checkUserId(user.getId());

        // Goi den VNPay de lay thong tin
        if (transaction.getPaymentType() == PaymentType.VNPAY) {
            TransactionInfoQuery transInfo = vnPayService.getTransactionInfo(transaction.getVnPay().getInvoiceCode(), transaction.getVnPay().getTimeCode(), request);

            // nếu giao dịch vnpay thành công
            if (transInfo.getTransactionStatus().equals("00") && transInfo.getAmount() != null && transInfo.getAmount().equals(String.valueOf(orderBill.getTotalPayment() * 100))) {
                transaction.setStatus(PaymentStatus.PAID);
                transaction.setTotalPaid(Long.parseLong(transInfo.getAmount().toString()) / 100);
            } else {
                orderBill.getOrderEventList().add(OrderEventEntity.builder()
                        .orderStatus(OrderStatus.CANCELED)
                        .description("Payment via VNPay failed")
                        .orderBill(orderBill)
                        .actor(ActorType.USER)
                        .build());
                orderBillRepository.save(orderBill);

                transaction.setTotalPaid(0L);
            }
        } else if (transaction.getPaymentType() == PaymentType.ZALOPAY) {
            GetOrderZaloPayResponse transResult = zaloPayService.getOrderTransactionInformation(transaction.getZaloPay().getAppTransactionId());

            if (transResult.getReturnCode() == 1 && transResult.getAmount() == orderBill.getTotalPayment()) {
                transaction.setStatus(PaymentStatus.PAID);
                transaction.setTotalPaid((long) transResult.getAmount());
                transaction.getZaloPay().setZalopayTransactionId(String.valueOf(transResult.getZpTransId()));
            } else if (transResult.getReturnCode() == 2) {
                orderBill.getOrderEventList().add(OrderEventEntity.builder()
                        .orderStatus(OrderStatus.CANCELED)
                        .description("Payment via ZaloPay failed")
                        .orderBill(orderBill)
                        .actor(ActorType.USER)
                        .build());
                transaction.getZaloPay().setZalopayTransactionId(String.valueOf(transResult.getZpTransId()));
                transaction.setTotalPaid(0L);
                orderBillRepository.save(orderBill);
            }
        }

        // Cập nhật kết quả từ vnpay vào database
        transactionRepository.save(transaction);
    }

    @Override
    public void completeTransaction(String transId) {
        OrderBillEntity orderBill = orderBillRepository.findByTransaction_Id(transId)
                .orElseThrow(() -> new ShopfeeException(ErrorConstant.NOT_FOUND, "Order bill with transaction id " + transId));
        TransactionEntity trans = orderBill.getTransaction();
        long totalPaid = orderBill.getTotalItemPrice();
        trans.setStatus(PaymentStatus.PAID);
        trans.setTotalPaid(totalPaid);
        transactionRepository.save(trans);
    }

    @Transactional
    public void refundOrder(OrderBillEntity orderBill, boolean refundCoin, boolean refundMoney) throws IOException, URISyntaxException {

        TransactionEntity transaction = orderBill.getTransaction();
        if(transaction.isRefunded()) {
            throw new ShopfeeException(ErrorConstant.ACTING_INCORRECTLY, "Order has been refunded");
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

                user.setCoin(user.getCoin() + coin);
                userRepository.save(user);
                transaction.setRefunded(true);
            }
        }
        if(refundMoney) {
            boolean isRefunded = refundTransaction(null, transaction);
            if(isRefunded) {
                transaction.setRefunded(true);
            } else {
                throw new ShopfeeException(ErrorConstant.SERVER_ERROR, "The payment side service failed, please try again later");
            }
        }
        transactionRepository.save(transaction);
    }

    public boolean refundTransaction(HttpServletRequest request, TransactionEntity transaction ) throws IOException, URISyntaxException {
        if (transaction.getPaymentType() == PaymentType.CASHING) {
            return false;
        }
        if (transaction.getPaymentType() == PaymentType.VNPAY) {
            Map<String, Object> responseRefund = vnPayService.refundOrder(request, transaction.getVnPay().getTimeCode(), transaction.getVnPay().getInvoiceCode(), transaction.getTotalPaid());
            String responseCode = responseRefund.get(VNPayConstant.VNP_RESPONSE_CODE).toString();
            if (responseCode.equals("00")) {
                // refund thafnh coong
                return true;
            } else {
                return false;
            }
        } else if (transaction.getPaymentType() == PaymentType.ZALOPAY) {
            RefundRequestDTO refundRequestDTO = new RefundRequestDTO();
            ZaloPayEntity zaloPay = transaction.getZaloPay();
            refundRequestDTO.setAmount(transaction.getTotalPaid());
            refundRequestDTO.setZpTransId(zaloPay.getZalopayTransactionId());
            refundRequestDTO.setDescription("");
            Map<String, Object> responseRefund = zaloPayService.sendRefund(refundRequestDTO);
            int returnCode = (int) responseRefund.getOrDefault("return_code", -1);
            if (returnCode == 1) {
                // refund thafnh coong
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
