package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.ProductStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetProductListResponse {
    private int totalPage;
    private List<Product> productList;

    @Data
    public static class Product {
        private String id;
//        private String code;
        private String name;
        private Long price;
        private String thumbnailUrl;
        private ProductStatus status;

        private static Product fromProductEntity(ProductEntity entity) {
            Product data = new Product();
            data.setId(entity.getId());
            data.setName(entity.getName());
            data.setPrice(entity.getPrice());
            data.setThumbnailUrl(entity.getImage().getThumbnailUrl());
            data.setStatus(entity.getStatus());
            return data;
        }
    }
    public static List<Product> fromProductEntityList(List<ProductEntity> entityList) {
        List<Product> data= new ArrayList<>();
        for(ProductEntity entity : entityList) {
            data.add(Product.fromProductEntity(entity));
        }
        return data;
    }
}
