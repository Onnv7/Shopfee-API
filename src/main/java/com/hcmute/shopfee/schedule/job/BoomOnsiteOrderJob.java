package com.hcmute.shopfee.schedule.job;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@RequiredArgsConstructor
public class BoomOnsiteOrderJob extends QuartzJobBean {
    public static final String ORDER_ID = "orderId";
    private final OrderBillRepository orderBillRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getJobDetail().getJobDataMap();

        OrderBillEntity orderBill = orderBillRepository.findById(data.getString(ORDER_ID))
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ORDER_BILL_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + data.getString(data.getString(ORDER_ID))));

        if(orderBill.getOrderEventList().get(0).getOrderStatus() == OrderStatus.PENDING_PICK_UP) {
            orderBill.getOrderEventList().add(OrderEventEntity.builder()
                            .orderStatus(OrderStatus.NOT_RECEIVED)
                            .actor(ActorType.AUTOMATIC)
                            .description(OrderStatus.NOT_RECEIVED.getResultDescription())
                            .note("There is no staff to continue processing this order")
                    .build());
            orderBillRepository.save(orderBill);
        }
    }
}
