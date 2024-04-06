package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.enums.AnswerStatus;
import lombok.Data;

@Data
public class ProcessCancellationDemandRequest {
    private AnswerStatus status;
}
