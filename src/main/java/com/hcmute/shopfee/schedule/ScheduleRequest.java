package com.hcmute.shopfee.schedule;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ScheduleRequest {
    @NotNull
    private String message;
    @NotNull
    private Date dateTime;
}
