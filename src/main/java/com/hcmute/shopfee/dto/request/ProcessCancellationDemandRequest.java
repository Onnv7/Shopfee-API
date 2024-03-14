package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.enums.CancellationRequestStatus;
import lombok.Data;

@Data
public class ProcessCancellationDemandRequest {
    private CancellationRequestStatus status;
}
