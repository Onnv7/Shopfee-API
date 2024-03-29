package com.hcmute.shopfee.dto.common;

import com.hcmute.shopfee.enums.ProductSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.NOT_EMPTY_DES;

@Data
public class ItemDetailDto {

    @Schema(example = PRODUCT_QUANTITY_EX)
    @Min(ORDER_QUANTITY_MIN)
    private Integer quantity;

    @Schema
    private List<String> toppingNameList;

    @Schema(example = PRODUCT_SIZE_EX)
    @NotNull
    private ProductSize size;

    @Schema(example = NOT_EMPTY_DES)
    private String note;
}
