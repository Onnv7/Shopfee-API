package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.enums.param.AnswerStatus;
import lombok.Data;

@Data
public class ProcessCancellationDemandRequest {
    private AnswerStatus status;
}
