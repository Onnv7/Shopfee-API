package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.payload.request.CreateCategoryRequest;
import com.hcmute.shopfee.payload.request.UpdateCategoryRequest;
import com.hcmute.shopfee.payload.response.CheckExistedNameResponse;
import com.hcmute.shopfee.payload.response.GetCategoryByIdResponse;
import com.hcmute.shopfee.payload.response.GetCategoryListResponse;
import com.hcmute.shopfee.payload.response.GetVisibleCategoryListResponse;

import java.util.List;

public interface ICategoryService {
    void createCategory(CreateCategoryRequest body) ;
    GetCategoryByIdResponse getCategoryById(String id);
    List<GetCategoryListResponse> getCategoryList();
    List<GetVisibleCategoryListResponse> getVisibleCategoryList();
    void updateCategory(UpdateCategoryRequest body, String id);
    void deleteCategoryById(String id);
    CheckExistedNameResponse isExistedCategoryName(String categoryName);
}
