package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.BranchProductStatus;
import com.hcmute.shopfee.enums.ProductStatus;
import lombok.Data;

@Data
public class GetTopSellingProductResponse {
    private String id;
    //    private String code;
    private String name;
    private Long price;
    private String thumbnailUrl;
    private BranchProductStatus status;
    private RatingSummaryDto ratingSummary;

    public static GetTopSellingProductResponse fromProductEntity(ProductEntity entity, RatingSummaryQueryDto ratingSummaryQueryDto, String branchId) {
        GetTopSellingProductResponse data = new GetTopSellingProductResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setPrice(entity.getPrice());
        data.setThumbnailUrl(entity.getImage().getThumbnailUrl());
        if(branchId != null) {
            data.setStatus(entity.getBranchProduct(branchId).getStatus());
        }
        data.setRatingSummary(RatingSummaryDto.fromRatingSummaryDto(ratingSummaryQueryDto));
        return data;
    }
}