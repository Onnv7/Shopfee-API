package com.hcmute.shopfee.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.enums.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class CreateOnsiteOrderRequest {

    @Schema(example = OBJECT_ID_EX)
    @NotBlank
    private String userId;

    @Schema()
    @NotEmpty
    private List<OrderItemDto> itemList;

    @Schema(example = COUPON_CODE_EX)
    private String productCouponCode;

    @Schema(example = COUPON_CODE_EX)
    private String orderCouponCode;

    @Schema(example = ORDER_NOTE_EX)
    private String note;

    @Schema(example = PAYMENT_TYPE_EX)
    @NotNull
    private PaymentType paymentType;

    @Schema(example = DATE_ISO_EX)
    @NotNull
    private Date receiveTime;

    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private String branchId;

    @Schema(example = PRODUCT_PRICE_EX)
    @NotNull
    private Long total;

    @Schema(example = COIN_EX)
    private Long coin;

    @Schema(example = FIRST_NAME_EX)
    @NotBlank
    private String recipientName;

    @Schema(example = PHONE_NUMBER_EX)
    @Pattern(regexp = PHONE_NUMBER_REGEX)
    @NotBlank
    private String phoneNumber;
}
