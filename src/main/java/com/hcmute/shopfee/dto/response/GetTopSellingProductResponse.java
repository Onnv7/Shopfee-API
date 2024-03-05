package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.database.product.ProductEntity;
import com.hcmute.shopfee.enums.ProductStatus;
import lombok.Data;

@Data
public class GetTopSellingProductResponse {
    private String id;
    //    private String code;
    private String name;
    private Long price;
    private String thumbnailUrl;
    private ProductStatus status;
    private RatingSummaryDto ratingSummary;

    public static GetTopSellingProductResponse fromProductEntity(ProductEntity entity, RatingSummaryQueryDto ratingSummaryQueryDto) {
        GetTopSellingProductResponse data = new GetTopSellingProductResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setPrice(entity.getPrice());
        data.setThumbnailUrl(entity.getThumbnailUrl());
        data.setStatus(entity.getStatus());
        data.setRatingSummary(RatingSummaryDto.fromRatingSummaryDto(ratingSummaryQueryDto));
        return data;
    }
}