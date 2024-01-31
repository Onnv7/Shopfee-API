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
public class CreateShippingOrderRequest {
    @Schema(example = OBJECT_ID_EX)
    @NotBlank
    private String userId;

    @Schema(description = NOT_EMPTY_DES)
    @NotEmpty
    private List<OrderItemDto> itemList;

    @Schema(example = ORDER_NOTE_EX)
    private String note;

    @Schema(example = PAYMENT_TYPE_EX)
    @NotNull
    private PaymentType paymentType;

    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private String addressId;

    @Schema(example = SHIPPING_FEE_EX)
    @NotNull
    private Long shippingFee;

    @Schema(example = COUPON_CODE_EX)
    private String orderCouponCode;

    @Schema(example = COUPON_CODE_EX)
    private String shippingCouponCode;

    @Schema(example = PRODUCT_PRICE_EX)
    @NotNull
    private Long total;
}
