package com.hcmute.shopfee.service.common;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.schedule.SchedulerUtils;
import com.hcmute.shopfee.schedule.job.AcceptOrderJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final Scheduler scheduler;

    public void setScheduler(Class<? extends Job> jobClass, Map<String, Object> data, Date startTime)  {
        try {
            JobDetail jobDetail = SchedulerUtils.buildJobDetail(jobClass, data);
            Trigger trigger = SchedulerUtils.buildTrigger(jobDetail, startTime);
            scheduler.scheduleJob(jobDetail, trigger);
        } catch(SchedulerException e) {
            throw new CustomException(ErrorConstant.SERVER_ERROR, "Scheduler service failed");
        }
    }
}
