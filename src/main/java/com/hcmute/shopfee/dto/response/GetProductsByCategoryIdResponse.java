package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.enums.ProductStatus;
import lombok.Data;

@Data
public class GetProductsByCategoryIdResponse {
    private String id;
    private String name;
    private String description;
    private double price;

    private String thumbnailUrl;
    private ProductStatus status;
}
