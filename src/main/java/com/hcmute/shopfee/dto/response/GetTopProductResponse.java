package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.database.product.ProductEntity;
import com.hcmute.shopfee.enums.ProductStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetTopProductResponse {
    private String id;
//    private String code;
    private String name;
    private Long price;
    private String description;
    private String thumbnailUrl;
    private ProductStatus status;
    private RatingSummaryDto ratingSummary;

    public static GetTopProductResponse fromProductEntity(ProductEntity entity, RatingSummaryQueryDto ratingSummaryQueryDto) {
        GetTopProductResponse data = new GetTopProductResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setPrice(entity.getPrice());
        data.setDescription(entity.getDescription());
        data.setThumbnailUrl(entity.getThumbnailUrl());
        data.setStatus(entity.getStatus());
        data.setRatingSummary(RatingSummaryDto.fromRatingSummaryDto(ratingSummaryQueryDto));
        return data;
    }

//    public static List<GetTopProductResponse> fromProductEntityList(List<ProductEntity> entityList) {
//        List<GetTopProductResponse> data = new ArrayList<>();
//        for (ProductEntity entity : entityList) {
//            data.add(fromProductEntity(entity));
//        }
//        return data;
//    }
}
