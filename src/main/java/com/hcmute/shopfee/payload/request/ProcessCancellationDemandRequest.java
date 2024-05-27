package com.hcmute.shopfee.payload.request;

import com.hcmute.shopfee.enums.param.AnswerStatus;
import lombok.Data;

@Data
public class ProcessCancellationDemandRequest {
    private AnswerStatus status;
}
