package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.ProductStatus;
import lombok.Data;

@Data
public class GetUserProductTrackingCardResponse {
    private String id;
    private String name;
    private String description;
    private double price;
    private String thumbnailUrl;
    private ProductStatus status;
    private RatingSummaryDto ratingSummary;

    public static GetUserProductTrackingCardResponse fromProductEntity(ProductEntity entity, RatingSummaryQueryDto ratingSummaryQueryDto) {
        GetUserProductTrackingCardResponse data = new GetUserProductTrackingCardResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setDescription(entity.getDescription());
        data.setPrice(entity.getPrice());
        data.setThumbnailUrl(entity.getImage().getThumbnailUrl());
        data.setStatus(entity.getStatus());
        data.setRatingSummary(RatingSummaryDto.fromRatingSummaryDto(ratingSummaryQueryDto));
        return data;
    }

    public static GetUserProductTrackingCardResponse fromProductIndex(ProductIndex entity, RatingSummaryQueryDto ratingSummaryQueryDto) {
        GetUserProductTrackingCardResponse data = new GetUserProductTrackingCardResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setDescription(entity.getDescription());
        data.setPrice(entity.getPrice());
        data.setThumbnailUrl(entity.getThumbnailUrl());
        data.setStatus(entity.getStatus());
        data.setRatingSummary(RatingSummaryDto.fromRatingSummaryDto(ratingSummaryQueryDto));
        return data;
    }
}
