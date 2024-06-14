package com.hcmute.shopfee.dto.common.coupon.reward;

import com.hcmute.shopfee.entity.sql.database.coupon.reward.ProductRewardEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class ProductRewardDto {
    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private String productId;

    @Schema(example = PRODUCT_SIZE_EX)
    private String productSize;

    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private Short quantity;

    public static ProductRewardDto fromProductRewardEntity(ProductRewardEntity entity) {
        ProductRewardDto data = new ProductRewardDto();
        data.setProductId(entity.getProduct().getId());
        data.setProductSize(entity.getProductSize());
        data.setQuantity(entity.getQuantity());
        return data;
    }
}
