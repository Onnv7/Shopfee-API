package com.hcmute.shopfee.payload.request;

import com.hcmute.shopfee.dto.common.OrderItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CheckShippingOrderItemRequest {
    @Schema
    @NotEmpty
    private List<OrderItemDto> orderItemList;

    @Schema
    @NotNull
    private String addressId;
}
