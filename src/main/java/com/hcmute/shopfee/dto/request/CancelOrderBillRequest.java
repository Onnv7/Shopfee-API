package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.ORDER_STATUS_DES_EX;

@Data
public class CancelOrderBillRequest {
    @Schema(example = ORDER_STATUS_DES_EX)
    private String description;
}
