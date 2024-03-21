package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.database.CategoryEntity;
import com.hcmute.shopfee.enums.CategoryStatus;
import lombok.Data;

@Data
public class GetCategoryByIdResponse {
    private String id;
    private String name;
    private String imageUrl;
    private CategoryStatus status;
    public static GetCategoryByIdResponse fromCategoryEntity(CategoryEntity entity) {
        GetCategoryByIdResponse data = new GetCategoryByIdResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setImageUrl(entity.getImage().getImageUrl());
        data.setStatus(entity.getStatus());
        return data;
    }
}
