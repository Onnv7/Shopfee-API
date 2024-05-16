package com.hcmute.shopfee.service.common;

import com.hcmute.shopfee.constant.ShopfeeConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.schedule.SchedulerUtils;
import com.hcmute.shopfee.schedule.job.BoomOnsiteOrderJob;
import com.hcmute.shopfee.schedule.job.RefuseOrderJob;
import com.hcmute.shopfee.schedule.job.CheckTransactionValidJob;
import com.hcmute.shopfee.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final Scheduler scheduler;

    public void setAutoBoomWhenNoOneReceiveOrder(String orderId, Date receiveTime) {
        Map<String, Object> schedulerData = new HashMap<String, Object>();
        Instant timeTrigger = DateUtils.plus(receiveTime.toInstant(), ShopfeeConstant.TIME_AFTER_PENDING_PICKUP_MINUTES, ChronoUnit.MINUTES);

        schedulerData.put(BoomOnsiteOrderJob.ORDER_ID, orderId);
        setScheduler(BoomOnsiteOrderJob.class, schedulerData, Date.from(timeTrigger));
    }
    public void setScheduleTransaction(TransactionEntity transaction) {
        Map<String, Object> checkTransactionData = new HashMap<String, Object>();
        Instant checkTransactionTime = transaction.getCreatedAt().toInstant();

        if (transaction.getPaymentType() == PaymentType.ZALOPAY) {
            checkTransactionTime = DateUtils.plus(checkTransactionTime, ShopfeeConstant.TIMEOUT_ZALO_TRANSACTION_MINUTES, ChronoUnit.MINUTES);
        } else if (transaction.getPaymentType() == PaymentType.VNPAY) {
            checkTransactionTime = DateUtils.plus(checkTransactionTime, ShopfeeConstant.TIMEOUT_VNPAY_TRANSACTION_MINUTES, ChronoUnit.MINUTES);
        }
        checkTransactionData.put(CheckTransactionValidJob.TRANSACTION_ID, transaction.getId());
        checkTransactionData.put(CheckTransactionValidJob.PAYMENT_TYPE, transaction.getPaymentType());
        setScheduler(CheckTransactionValidJob.class, checkTransactionData, Date.from(checkTransactionTime));
    }

    public void setAutoCancelOrder(OrderBillEntity orderBill) {
        Instant newIn = DateUtils.plus(orderBill.getCreatedAt().toInstant(), ShopfeeConstant.TIMEOUT_REFUSE_ORDER_MINUTES, ChronoUnit.MINUTES);
        Map<String, Object> orderAcceptanceData = new HashMap<String, Object>();
        orderAcceptanceData.put(RefuseOrderJob.ORDER_BILL_ID, orderBill.getId());
        setScheduler(RefuseOrderJob.class, orderAcceptanceData, Date.from(newIn));
    }

    public void setScheduler(Class<? extends Job> jobClass, Map<String, Object> data, Date startTime)  {
        try {
            JobDetail jobDetail = SchedulerUtils.buildJobDetail(jobClass, data);
            Trigger trigger = SchedulerUtils.buildTrigger(jobDetail, startTime);
            scheduler.scheduleJob(jobDetail, trigger);
        } catch(SchedulerException e) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.SERVER_ERROR, "Scheduler service failed");
        }
    }
}
