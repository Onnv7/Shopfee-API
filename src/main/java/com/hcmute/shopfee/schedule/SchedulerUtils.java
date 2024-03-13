package com.hcmute.shopfee.schedule;

import org.quartz.*;

import java.util.*;

public class SchedulerUtils {
    public static JobDetail buildJobDetail(Class<? extends Job> jobClass, Map<String, Object> data) {
        JobDataMap jobDataMap = new JobDataMap();
        if(data != null) {

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                jobDataMap.put(key, value);
            }
        }


        return JobBuilder.newJob(jobClass)
                .withIdentity(UUID.randomUUID().toString(), "group-job")
                .withDescription("Job details description")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    public static Trigger buildTrigger(JobDetail jobDetail, Date startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "group-trigger")
                .withDescription("Trigger description")
                .startAt(startAt)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
