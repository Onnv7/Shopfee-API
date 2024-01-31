package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.enums.ProductStatus;
import lombok.Data;

@Data
public class GetAllVisibleProductResponse {
    private String id;
    private String code;
    private String name;
    private double price;
    private String thumbnailUrl;
    private ProductStatus status;
}
