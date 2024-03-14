package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateOrderResponse {
    private String orderId;
    private String paymentUrl;
    private String transactionId;
    private String branchId;
}

