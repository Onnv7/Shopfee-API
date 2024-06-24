package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.BranchProductStatus;
import com.hcmute.shopfee.enums.ProductStatus;
import lombok.Data;

@Data
public class GetProductRecommendResponse {
    private String id;
    private String name;
    private Long price;
    private String thumbnailUrl;
    private ProductStatus status;
    private BranchProductStatus branchProductStatus;
    private RatingSummaryDto ratingSummary;

    public static GetProductRecommendResponse fromProductEntity(ProductEntity entity, String branchId) {
        GetProductRecommendResponse data = new GetProductRecommendResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setPrice(entity.getPrice());
        data.setThumbnailUrl(entity.getImage().getThumbnailUrl());
        data.setStatus(entity.getStatus());
        if (branchId != null) {
            data.setBranchProductStatus(entity.getBranchProduct(branchId).getStatus());
        }
        return data;
    }
}
