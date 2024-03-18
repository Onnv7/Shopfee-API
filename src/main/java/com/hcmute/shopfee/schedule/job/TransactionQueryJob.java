package com.hcmute.shopfee.schedule.job;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.database.order.OrderEventEntity;
import com.hcmute.shopfee.entity.database.order.TransactionEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.module.vnpay.querydr.response.TransactionInfoQuery;
import com.hcmute.shopfee.module.zalopay.order.dto.response.GetOrderZaloPayResponse;
import com.hcmute.shopfee.repository.database.TransactionRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.service.common.VNPayService;
import com.hcmute.shopfee.service.common.ZaloPayService;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.QuartzJobBean;

@RequiredArgsConstructor
public class TransactionQueryJob extends QuartzJobBean {
    public static final String TRANSACTION_ID = "transactionId";
    public static final String PAYMENT_TYPE = "paymentType";
    private final ZaloPayService zaloPayService;
    private final VNPayService vnPayService;
    private final TransactionRepository transactionRepository;
    private final OrderBillRepository orderBillRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        TransactionEntity transaction = transactionRepository.findById(data.getString(TRANSACTION_ID)).
                orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.TRANSACTION_ID_NOT_FOUND + data.getString(TRANSACTION_ID)));
        if (transaction.getPaymentType() == PaymentType.ZALOPAY) {
            GetOrderZaloPayResponse zaloResult = zaloPayService.getOrder(transaction.getInvoiceCode());
            if (zaloResult.getReturnCode() == 1) {
                transaction.setStatus(PaymentStatus.PAID);
                transaction.setTotalPaid((long) zaloResult.getAmount());
                transactionRepository.save(transaction);
            } else if (zaloResult.getReturnCode() == 2) {
                transaction.setStatus(PaymentStatus.UNPAID);
                transaction.setTotalPaid((long) zaloResult.getAmount());

                OrderBillEntity orderBill = transaction.getOrderBill();
                orderBill.getOrderEventList().add(OrderEventEntity.builder()
                        .orderStatus(OrderStatus.CANCELED)
                        .description("Payment failed, order canceled")
                        .isEmployee(true)
                        .orderBill(orderBill)
                        .build());
                orderBillRepository.save(orderBill);
            }
        } else if (transaction.getPaymentType() == PaymentType.VNPAY) {
            TransactionInfoQuery vnpayResult = vnPayService.getTransactionInfo(transaction.getInvoiceCode(), transaction.getTimeCode(), null);
            if(vnpayResult.getTransactionStatus().equals("00")) {
                transaction.setStatus(PaymentStatus.PAID);
                transaction.setTotalPaid(Long.valueOf(vnpayResult.getAmount()));
                transactionRepository.save(transaction);
            }
        }
    }
}
