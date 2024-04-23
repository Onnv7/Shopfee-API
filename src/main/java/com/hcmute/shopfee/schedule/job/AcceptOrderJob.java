package com.hcmute.shopfee.schedule.job;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import com.hcmute.shopfee.service.common.AuditorAwareService;
import com.hcmute.shopfee.service.common.VNPayService;
import com.hcmute.shopfee.service.common.ZaloPayService;
import com.hcmute.shopfee.service.core.ITransactionService;
import com.hcmute.shopfee.service.core.impl.TransactionService;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
public class AcceptOrderJob extends QuartzJobBean {
    public static final String ORDER_BILL_ID = "orderBillId";
    private final OrderEventRepository orderEventRepository;
    private final OrderBillRepository orderBillRepository;
    private final AuditorAwareService auditorAwareService;
    private final VNPayService vnPayService;
    private final ZaloPayService zaloPayService;
    private final ITransactionService transactionService;
    @Transactional
    @Override
    protected void executeInternal(JobExecutionContext context) {
        OrderBillEntity orderBill = orderBillRepository.findById(context.getJobDetail().getJobDataMap().getString(ORDER_BILL_ID))
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ORDER_BILL_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + context.getJobDetail().getJobDataMap().getString("orderBillId")));
        List<OrderEventEntity> orderEvent = orderBill.getOrderEventList();
        if(orderEvent != null && orderBill.getOrderEventList().get(0).getOrderStatus() == OrderStatus.CREATED) {
            TransactionEntity transaction = orderBill.getTransaction();
            OrderEventEntity newEvent= OrderEventEntity.builder()
                    .orderBill(orderBill)
                    .orderStatus(OrderStatus.CANCELED)
                    .createdBy(auditorAwareService.getCurrentAuditor().orElse("AUTOMATIC"))
                    .description("The order was canceled because there was no staff to receive the order")
                    .actor(ActorType.AUTOMATIC)
                    .build();
           transactionService.refundOrder(orderBill, true, true);
            orderBill.getOrderEventList().add(newEvent);
                orderBillRepository.save(orderBill);
        }
    }
}
