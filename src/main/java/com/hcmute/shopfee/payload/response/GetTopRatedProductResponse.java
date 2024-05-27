package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.BranchProductStatus;
import lombok.Data;

@Data
public class GetTopRatedProductResponse {
    private String id;
//    private String code;
    private String name;
    private Long price;
    private String description;
    private String thumbnailUrl;
    private BranchProductStatus status;
    private RatingSummaryDto ratingSummary;

    public static GetTopRatedProductResponse fromProductEntity(ProductEntity entity, RatingSummaryQueryDto ratingSummaryQueryDto, String branchId) {
        GetTopRatedProductResponse data = new GetTopRatedProductResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setPrice(entity.getPrice());
        data.setDescription(entity.getDescription());
        data.setThumbnailUrl(entity.getImage().getThumbnailUrl());
        if(branchId != null) {
            data.setStatus(entity.getBranchProduct(branchId).getStatus());
        }
        data.setRatingSummary(RatingSummaryDto.fromRatingSummaryDto(ratingSummaryQueryDto));
        return data;
    }

}
