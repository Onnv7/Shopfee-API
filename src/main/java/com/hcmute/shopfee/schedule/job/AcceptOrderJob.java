package com.hcmute.shopfee.schedule.job;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.database.order.OrderEventEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import com.hcmute.shopfee.service.common.AuditorAwareService;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
public class AcceptOrderJob extends QuartzJobBean {
    public static final String orderBillId = "orderBillId";
    private final OrderEventRepository orderEventRepository;
    private final OrderBillRepository orderBillRepository;
    private final AuditorAwareService auditorAwareService;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        OrderBillEntity orderBill = orderBillRepository.findById(context.getJobDetail().getJobDataMap().getString(orderBillId))
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + context.getJobDetail().getJobDataMap().getString("orderBillId")));
        List<OrderEventEntity> orderEvent = orderBill.getOrderEventList();
        if(orderEvent != null && orderBill.getOrderEventList().get(0).getOrderStatus() == OrderStatus.CREATED) {
            OrderEventEntity newEvent = OrderEventEntity.builder()
                    .orderBill(orderBill)
                    .orderStatus(OrderStatus.ACCEPTED)
                    .createdBy(auditorAwareService.getCurrentAuditor().orElse("AUTOMATIC"))
                    .description("The order has been automatically accepted")
                    .isEmployee(false)
                    .build();
            orderBill.getOrderEventList().add(newEvent);
            try {
                orderBillRepository.save(orderBill);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
