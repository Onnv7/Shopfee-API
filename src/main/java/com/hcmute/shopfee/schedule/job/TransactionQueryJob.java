package com.hcmute.shopfee.schedule.job;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.dto.common.vnpay.TransactionInfoQuery;
import com.hcmute.shopfee.dto.common.zalopay.GetOrderZaloPayResponse;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.service.common.AuditorAwareService;
import com.hcmute.shopfee.service.common.VNPayService;
import com.hcmute.shopfee.service.common.ZaloPayService;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@RequiredArgsConstructor
public class TransactionQueryJob extends QuartzJobBean {
    public static final String TRANSACTION_ID = "transactionId";
    public static final String PAYMENT_TYPE = "paymentType";
    private final ZaloPayService zaloPayService;
    private final VNPayService vnPayService;
    private final TransactionRepository transactionRepository;
    private final OrderBillRepository orderBillRepository;
    private final AuditorAwareService auditorAwareService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        TransactionEntity transaction = transactionRepository.findById(data.getString(TRANSACTION_ID)).
                orElseThrow(() -> new ShopfeeException(ErrorConstant.NOT_FOUND, ErrorConstant.TRANSACTION_ID_NOT_FOUND + data.getString(TRANSACTION_ID)));

        if (transaction.getPaymentType() == PaymentType.ZALOPAY) {
            GetOrderZaloPayResponse zaloResult = zaloPayService.getOrderTransactionInformation(transaction.getZaloPay().getAppTransactionId());
            if (zaloResult.getReturnCode() == 1) {
                transaction.setStatus(PaymentStatus.PAID);
                transaction.setTotalPaid((long) zaloResult.getAmount());
            } else if (zaloResult.getReturnCode() == 2) {
                transaction.setStatus(PaymentStatus.UNPAID);
                transaction.setTotalPaid((long) zaloResult.getAmount());

                OrderBillEntity orderBill = transaction.getOrderBill();
                orderBill.getOrderEventList().add(OrderEventEntity.builder()
                        .orderStatus(OrderStatus.CANCELED)
                        .description("Payment failed, order canceled")
                        .actor(ActorType.AUTOMATIC)
                        .createdBy(auditorAwareService.getCurrentAuditor().orElse("AUTOMATIC"))
                        .orderBill(orderBill)
                        .build());
                orderBillRepository.save(orderBill);
            }
        } else if (transaction.getPaymentType() == PaymentType.VNPAY) {
            TransactionInfoQuery vnpayResult = vnPayService.getTransactionInfo(transaction.getVnPay().getInvoiceCode(), transaction.getVnPay().getTimeCode(), null);
            if(vnpayResult.getTransactionStatus().equals("00")) {
                transaction.setStatus(PaymentStatus.PAID);
                transaction.setTotalPaid(Long.valueOf(vnpayResult.getAmount()));
            } else {
                transaction.setStatus(PaymentStatus.UNPAID);
                transaction.setTotalPaid(0L);
            }
        }
        transactionRepository.save(transaction);
    }
}
