package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.enums.ProductStatus;
import lombok.Data;

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
        private double price;
        private String thumbnailUrl;
        private ProductStatus status;
    }
}
