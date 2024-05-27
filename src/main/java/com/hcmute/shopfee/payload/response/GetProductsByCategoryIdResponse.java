package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.BranchProductStatus;
import com.hcmute.shopfee.enums.ProductStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetProductsByCategoryIdResponse {
    private Integer totalPage;
    private List<ProductCard> productList;

    @Data
    public static class ProductCard {

        private String id;
        private String name;
        private String description;
        private double price;
        private String thumbnailUrl;
        private BranchProductStatus status;
        private RatingSummaryDto ratingSummary;
        public static ProductCard fromProductEntity(ProductEntity entity, RatingSummaryQueryDto ratingSummaryQueryDto) {
            ProductCard data = new ProductCard();
            data.setId(entity.getId());
            data.setName(entity.getName());
            data.setDescription(entity.getDescription());
            data.setPrice(entity.getPrice());
            data.setThumbnailUrl(entity.getImage().getThumbnailUrl());
            data.setRatingSummary(RatingSummaryDto.fromRatingSummaryDto(ratingSummaryQueryDto));
            return data;
        }
    }

}
