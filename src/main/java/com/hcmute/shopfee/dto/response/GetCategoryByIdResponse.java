package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.enums.CategoryStatus;
import lombok.Data;

@Data
public class GetCategoryByIdResponse {
    private String id;
//    private String code;
    private String name;
    private String imageUrl;
    private CategoryStatus status;
}
