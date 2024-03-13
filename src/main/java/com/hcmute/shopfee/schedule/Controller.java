package com.hcmute.shopfee.schedule;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import static com.hcmute.shopfee.constant.SwaggerConstant.ADDRESS_CONTROLLER_TITLE;

@Tag(name = "Scheduled")
@RestController
public class Controller {
    @Autowired
    private Scheduler scheduler;


    @PostMapping("/scheduled")
    public ResponseEntity<?> scheduled(@RequestBody ScheduleRequest body) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(body);
        Trigger trigger = buildTrigger(jobDetail, body.getDateTime());
        scheduler.scheduleJob(jobDetail, trigger);
        return ResponseEntity.ok("okok");
    }
    private JobDetail buildJobDetail(ScheduleRequest scheduleRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("message", scheduleRequest.getMessage());
        jobDataMap.put("dateTime", scheduleRequest.getDateTime());

        return JobBuilder.newJob(ScheduleJob.class)
                .withIdentity(UUID.randomUUID().toString(), "group-job")
                .withDescription("Job details description")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }
    private Trigger buildTrigger(JobDetail jobDetail, Date startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "group-trigger")
                .withDescription("Trigger description")
                .startAt(startAt)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
