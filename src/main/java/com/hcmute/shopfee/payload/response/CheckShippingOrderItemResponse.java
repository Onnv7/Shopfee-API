package com.hcmute.shopfee.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckShippingOrderItemResponse {
    private List<BranchInvalid> branchInvalidList;
    private BranchValid branchValid;

    @Data
    public static class BranchValid {
        private String branchId;
        private Integer shippingFee;
    }

    @Data
    public static class BranchInvalid {
        private String branchId;
        private List<OrderItemInvalid> orderItemInvalidList;
    }
    @Data
    public static class OrderItemInvalid {
        private String productId;

        public OrderItemInvalid(String productId) {
            this.productId = productId;
        }
    }
}
