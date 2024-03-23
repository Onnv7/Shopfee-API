package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.sql.database.CategoryEntity;
import com.hcmute.shopfee.enums.CategoryStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetCategoryListResponse {
    private String id;
//    private String code;
    private String name;
    private String imageUrl;
    private CategoryStatus status;
    private static GetCategoryListResponse fromCategoryEntity(CategoryEntity entity) {
        GetCategoryListResponse data = new GetCategoryListResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setImageUrl(entity.getImage().getImageUrl());
        data.setStatus(entity.getStatus());
        return data;
    }
    public static List<GetCategoryListResponse> fromCategoryEntityList(List<CategoryEntity> entityList) {
        List<GetCategoryListResponse> data = new ArrayList<>();
        for (CategoryEntity entity : entityList) {
            data.add(fromCategoryEntity(entity));
        }
        return data;
    }
}
