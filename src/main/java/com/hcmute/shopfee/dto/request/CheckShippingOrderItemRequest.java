package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.dto.common.OrderItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CheckShippingOrderItemRequest {
    @Schema
    @NotEmpty
    private List<OrderItemDto> orderItemList;
    private Location deliveryLocation;
    @Data
    public static class Location {
        private Double lng;
        private Double lat;
    }
}
