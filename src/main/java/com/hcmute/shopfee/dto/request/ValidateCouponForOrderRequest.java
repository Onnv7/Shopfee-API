package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.enums.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class ValidateCouponForOrderRequest {
    @Schema(example = OBJECT_ID_EX)
    @NotBlank
    private String userId;

    @Schema()
    @NotEmpty
    private List<OrderItemDto> itemList;

    @Schema(example = SHIPPING_FEE_EX)
    private Long shippingFee;

    @Schema(example = COUPON_CODE_EX)
    private String productCouponCode;

    @Schema(example = COUPON_CODE_EX)
    private String orderCouponCode;

    @Schema(example = COUPON_CODE_EX)
    private String shippingCouponCode;

    @Schema(example = PRODUCT_PRICE_EX)
    @NotNull
    private Long total;
}
