package com.hcmute.shopfee.dto.common;

import com.hcmute.shopfee.entity.order.OrderItemEntity;
import com.hcmute.shopfee.enums.ProductSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class OrderItemDto {
    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private String productId;

    @Schema()
    @NotEmpty
    private List<ItemDetailDto> itemDetailList;

//    @Schema(example = COUPON_CODE_EX)
//    private String couponProductCode;


}
