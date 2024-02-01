package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class UpdateOrderStatusRequest {
    @Schema(example = ORDER_STATUS_EX)
    @NotNull
    private OrderStatus orderStatus;
    @Schema(example = ORDER_STATUS_DES_EX)
    private String description;
}
