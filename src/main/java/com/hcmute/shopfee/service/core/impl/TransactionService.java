package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.database.TransactionEntity;
import com.hcmute.shopfee.entity.database.UserEntity;
import com.hcmute.shopfee.entity.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.database.order.OrderEventEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.TransactionRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.service.core.IOrderService;
import com.hcmute.shopfee.service.core.ITransactionService;
import com.hcmute.shopfee.utils.SecurityUtils;
import com.hcmute.shopfee.utils.VNPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;
    private final IOrderService orderService;
    private final OrderBillRepository orderBillRepository;

    @Transactional
    @Override
    public void updateTransaction(String id, HttpServletRequest request) {
        TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Transaction with id " + id));

        OrderBillEntity orderBill = transaction.getOrderBill();
        UserEntity user = orderBill.getUser();

        SecurityUtils.checkUserId(user.getId());

        // Goi den VNPay de lay thong tin
        Map<String, Object> transInfo = null;
        try {
            transInfo = VNPayUtils.getTransactionInfo(transaction.getInvoiceCode(), transaction.getTimeCode(), request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // nếu giao dịch vnpay thành công
        if (transInfo.get("vnp_TransactionStatus").equals("00") && transInfo.get("vnp_Amount").equals(String.valueOf(orderBill.getTotalItemPrice() * 100))) {
            transaction.setStatus(PaymentStatus.PAID);
            transaction.setTotalPaid(Long.parseLong(transInfo.get("vnp_Amount").toString()) / 100);
        } else {
            orderBill.getOrderEventList().add(OrderEventEntity.builder()
                    .orderStatus(OrderStatus.CANCELED)
                    .description("Payment failed")
                    .orderBill(orderBill)
                    .isEmployee(false)
                    .build());

            user.setCoin(user.getCoin() + orderBill.getTotalPayment());
            orderBillRepository.save(orderBill);

            transaction.setStatus(PaymentStatus.REFUNDED);
            transaction.setTotalPaid(0L);
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
