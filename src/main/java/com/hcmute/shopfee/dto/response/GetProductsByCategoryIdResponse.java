package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
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
        private ProductStatus status;
        private RatingSummaryDto ratingSummary;
        public static ProductCard fromProductEntity(ProductEntity entity) {
            ProductCard data = new ProductCard();
            data.setId(entity.getId());
            data.setName(entity.getName());
            data.setDescription(entity.getDescription());
            data.setPrice(entity.getPrice());
            data.setThumbnailUrl(entity.getImage().getThumbnailUrl());
            data.setStatus(entity.getStatus());
            data.setRatingSummary(entity.getRatingSummary());
            return data;
        }
    }


    public static List<ProductCard> fromProductEntityList(List<ProductEntity> entityList) {
        List<ProductCard> data = new ArrayList<ProductCard>();
        for (ProductEntity entity: entityList) {
            data.add(ProductCard.fromProductEntity(entity));
        }
        return data;
    }
}
