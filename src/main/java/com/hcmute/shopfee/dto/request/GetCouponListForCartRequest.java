package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.dto.common.OrderItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class GetCouponListForCartRequest {

    @Schema
    @NotEmpty
    private List<OrderItemDto> orderItemList;

    @Schema(example = SHIPPING_FEE_EX)
    private String shippingFee;

    @Schema(example = TOTAL_PAID_EX)
    @NotNull
    private Long totalPayment;

    @Schema(example = TOTAL_ITEM_PRICE_EX)
    @NotNull
    private Long totalItemPrice;

    @Schema(example = COUPON_CODE_EX)
    private String shippingCouponCode;

    @Schema(example = COUPON_CODE_EX)
    private String orderCouponCode;

    @Schema(example = COUPON_CODE_EX)
    private String productCouponCode;
}
