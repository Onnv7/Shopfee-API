package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.BranchProductStatus;
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
        private BranchProductStatus branchProductStatus;
        private RatingSummaryDto ratingSummary;

        private static Product fromProductEntity(ProductEntity entity, String branchId) {
            Product data = new Product();
            data.setId(entity.getId());
            data.setName(entity.getName());
            data.setPrice(entity.getPrice());
            data.setThumbnailUrl(entity.getImage().getThumbnailUrl());
            data.setStatus(entity.getStatus());
            if(branchId != null) {
                data.setBranchProductStatus(entity.getBranchProduct(branchId).getStatus());
            }
            return data;
        }
        private static Product fromProductIndex(ProductIndex index) {
            Product data = new Product();
            data.setId(index.getId());
            data.setName(index.getName());
            data.setPrice(index.getPrice());
            data.setThumbnailUrl(index.getThumbnailUrl());
            data.setStatus(index.getStatus());
            return data;
        }
    }
    public static List<Product> fromProductEntityList(List<ProductEntity> entityList, String branchId) {
        List<Product> data= new ArrayList<>();
        for(ProductEntity entity : entityList) {
            data.add(Product.fromProductEntity(entity, branchId));
        }
        return data;
    }

    public static List<Product> fromProductIndexList(List<ProductIndex> indexList) {
        List<Product> data= new ArrayList<>();
        for(ProductIndex entity : indexList) {
            data.add(Product.fromProductIndex(entity));
        }
        return data;
    }
}
