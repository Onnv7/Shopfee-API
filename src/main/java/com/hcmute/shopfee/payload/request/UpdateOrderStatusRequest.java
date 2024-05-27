package com.hcmute.shopfee.payload.request;

import com.hcmute.shopfee.statemachine.OrderEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class UpdateOrderStatusRequest {
//    @Schema(example = ORDER_STATUS_EX)
//    @NotNull
//    private OrderStatus orderStatus;

    @Schema(example = ORDER_STATUS_DES_EX)
    private String note;

    @Schema(example = ORDER_EVENT_EX)
    private OrderEvent event;
}
