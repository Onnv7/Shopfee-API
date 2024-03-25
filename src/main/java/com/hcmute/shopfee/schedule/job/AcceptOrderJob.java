package com.hcmute.shopfee.schedule.job;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.entity.sql.database.order.TransactionEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import com.hcmute.shopfee.service.common.AuditorAwareService;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;


@RequiredArgsConstructor
public class AcceptOrderJob extends QuartzJobBean {
    public static final String ORDER_BILL_ID = "orderBillId";
    private final OrderEventRepository orderEventRepository;
    private final OrderBillRepository orderBillRepository;
    private final AuditorAwareService auditorAwareService;
    @Override
    protected void executeInternal(JobExecutionContext context) {
        OrderBillEntity orderBill = orderBillRepository.findById(context.getJobDetail().getJobDataMap().getString(ORDER_BILL_ID))
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + context.getJobDetail().getJobDataMap().getString("orderBillId")));
        List<OrderEventEntity> orderEvent = orderBill.getOrderEventList();
        if(orderEvent != null && orderBill.getOrderEventList().get(0).getOrderStatus() == OrderStatus.CREATED) {
            TransactionEntity transaction = orderBill.getTransaction();
            OrderEventEntity newEvent;
            if(transaction.getPaymentType() != PaymentType.CASHING && transaction.getStatus() == PaymentStatus.UNPAID) {
                newEvent = OrderEventEntity.builder()
                        .orderBill(orderBill)
                        .orderStatus(OrderStatus.CANCELED)
                        .createdBy(auditorAwareService.getCurrentAuditor().orElse("AUTOMATIC"))
                        .description("The order has been canceled due to unpaid payment")
                        .actor(ActorType.AUTOMATIC)
                        .build();
            } else {
                newEvent = OrderEventEntity.builder()
                        .orderBill(orderBill)
                        .orderStatus(OrderStatus.ACCEPTED)
                        .createdBy(auditorAwareService.getCurrentAuditor().orElse("AUTOMATIC"))
                        .description("The order has been automatically accepted")
                        .actor(ActorType.AUTOMATIC)
                        .build();
            }
            orderBill.getOrderEventList().add(newEvent);
                orderBillRepository.save(orderBill);
        }
    }
}
