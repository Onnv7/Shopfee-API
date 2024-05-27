package com.hcmute.shopfee.payload.request;

import com.hcmute.shopfee.dto.common.OrderItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CheckTakeAwayOrderItemRequest {
    @Schema
    @NotEmpty
    private List<OrderItemDto> orderItemList;
}
