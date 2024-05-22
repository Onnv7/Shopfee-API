package com.hcmute.shopfee.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CheckTakeAwayOrderItemResponse {
    private List<OrderItemInvalid> orderItemInvalidList;

    @Data
    public static class OrderItemInvalid {
        private String productId;

        public OrderItemInvalid(String productId) {
            this.productId = productId;
        }
    }
}
