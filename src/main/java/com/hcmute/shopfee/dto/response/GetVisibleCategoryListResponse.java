package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.database.CategoryEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetVisibleCategoryListResponse {
    private String id;
    private String name;
    private String imageUrl;

    private static GetVisibleCategoryListResponse fromCategoryEntity(CategoryEntity entity) {
        GetVisibleCategoryListResponse data = new GetVisibleCategoryListResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setImageUrl(entity.getImage().getImageUrl());
        return data;
    }
    public static List<GetVisibleCategoryListResponse> fromCategoryEntityList(List<CategoryEntity> entityList) {
        List<GetVisibleCategoryListResponse> data = new ArrayList<>();
        for (CategoryEntity entity : entityList) {
            data.add(fromCategoryEntity(entity));
        }
        return data;
    }
}
