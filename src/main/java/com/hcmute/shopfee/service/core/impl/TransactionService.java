package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.order.TransactionEntity;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.module.vnpay.querydr.response.TransactionInfoQuery;
import com.hcmute.shopfee.module.zalopay.order.dto.response.GetOrderZaloPayResponse;
import com.hcmute.shopfee.repository.database.TransactionRepository;
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

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;
    private final IOrderService orderService;
    private final OrderBillRepository orderBillRepository;
    private final VNPayService vnPayService;
    private final ZaloPayService zaloPayService;
    private final AuditorAwareService auditorAwareService;

    @Transactional
    @Override
    public void updateTransaction(String id, HttpServletRequest request) {
        TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Transaction with id " + id));

        OrderBillEntity orderBill = transaction.getOrderBill();
        UserEntity user = orderBill.getUser();

        SecurityUtils.checkUserId(user.getId());

        // Goi den VNPay de lay thong tin
        if (transaction.getPaymentType() == PaymentType.VNPAY) {
            TransactionInfoQuery transInfo = vnPayService.getTransactionInfo(transaction.getInvoiceCode(), transaction.getTimeCode(), request);
            ;

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
            GetOrderZaloPayResponse transResult = zaloPayService.getOrderTransactionInformation(transaction.getInvoiceCode());

            if (transResult.getReturnCode() == 1 && transResult.getAmount() == orderBill.getTotalPayment()) {
                transaction.setStatus(PaymentStatus.PAID);
                transaction.setTotalPaid((long) transResult.getAmount());
            } else if (transResult.getReturnCode() == 2) {
                orderBill.getOrderEventList().add(OrderEventEntity.builder()
                        .orderStatus(OrderStatus.CANCELED)
                        .description("Payment via ZaloPay failed")
                        .orderBill(orderBill)
                        .actor(ActorType.USER)
                        .build());
                orderBillRepository.save(orderBill);
                transaction.setTotalPaid(0L);
            }
        }

        // Cập nhật kết quả từ vnpay vào database
        transactionRepository.save(transaction);
    }

    @Override
    public void completeTransaction(String transId) {
        OrderBillEntity orderBill = orderBillRepository.findByTransaction_Id(transId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Order bill with transaction id " + transId));
        TransactionEntity trans = orderBill.getTransaction();
        long totalPaid = orderBill.getTotalItemPrice();
        trans.setStatus(PaymentStatus.PAID);
        trans.setTotalPaid(totalPaid);
        transactionRepository.save(trans);
    }
}
