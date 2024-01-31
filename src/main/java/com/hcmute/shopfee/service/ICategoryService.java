package com.hcmute.shopfee.service;

import com.hcmute.shopfee.dto.request.CreateCategoryRequest;
import com.hcmute.shopfee.dto.request.UpdateCategoryRequest;
import com.hcmute.shopfee.dto.response.GetCategoryByIdResponse;
import com.hcmute.shopfee.dto.response.GetCategoryListResponse;
import com.hcmute.shopfee.dto.response.GetVisibleCategoryListResponse;

import java.util.List;

public interface ICategoryService {
    void createCategory(CreateCategoryRequest body) ;
    GetCategoryByIdResponse getCategoryById(String id);
    List<GetCategoryListResponse> getCategoryList();
    List<GetVisibleCategoryListResponse> getVisibleCategoryList();
    void updateCategory(UpdateCategoryRequest body, String id);
    void deleteCategoryById(String id);
}
