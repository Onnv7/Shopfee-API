package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.CreateCategoryRequest;
import com.hcmute.shopfee.dto.request.UpdateCategoryRequest;
import com.hcmute.shopfee.dto.response.GetCategoryByIdResponse;
import com.hcmute.shopfee.dto.response.GetCategoryListResponse;
import com.hcmute.shopfee.dto.response.GetVisibleCategoryListResponse;
import com.hcmute.shopfee.entity.database.CategoryEntity;
import com.hcmute.shopfee.enums.CategoryStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.CategoryRepository;
import com.hcmute.shopfee.service.core.ICategoryService;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.ImageUtils;
import com.hcmute.shopfee.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final ModelMapperService modelMapperService;

    @Override
    public void createCategory(CreateCategoryRequest body) {
        String cgrName = body.getName();
        CategoryEntity existedCategory = categoryRepository.findByIsDeletedFalseAndName(cgrName).orElse(null);
        if (existedCategory != null) {
            throw new CustomException(ErrorConstant.CATEGORY_EXISTED);
        }
        byte[] originalImage = new byte[0];
        try {
            originalImage = body.getImage().getBytes();
            byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
            HashMap<String, String> fileUploaded = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CATEGORY_PATH,
                    StringUtils.generateFileName(body.getName(), "category"), newImage);

            CategoryEntity category = CategoryEntity.builder()
                    .imageUrl(fileUploaded.get(CloudinaryConstant.URL_PROPERTY))
                    .imageId(fileUploaded.get(CloudinaryConstant.PUBLIC_ID))
                    .name(body.getName())
                    .status(CategoryStatus.HIDDEN)
                    .isDeleted(false)
                    .build();
            categoryRepository.save(category);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GetCategoryByIdResponse getCategoryById(String id) {
        CategoryEntity category = categoryRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));
        return modelMapperService.mapClass(category, GetCategoryByIdResponse.class);
    }

    @Override
    public List<GetCategoryListResponse> getCategoryList() {
        List<CategoryEntity> categoryList = categoryRepository.findByIsDeletedFalse();
        return modelMapperService.mapList(categoryList, GetCategoryListResponse.class);
    }

    @Override
    public List<GetVisibleCategoryListResponse> getVisibleCategoryList() {
        List<CategoryEntity> categoryList = categoryRepository.findByIsDeletedFalseAndStatus(CategoryStatus.VISIBLE);
        return modelMapperService.mapList(categoryList, GetVisibleCategoryListResponse.class);
    }

    @Override
    public void updateCategory(UpdateCategoryRequest body, String id) {
        CategoryEntity category = categoryRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));;
        if (body.getImage() != null) {
            try {
                cloudinaryService.deleteImage(category.getImageId());
                byte[] originalImage = body.getImage().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);

                HashMap<String, String> fileUploaded = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CATEGORY_PATH, StringUtils.generateFileName(body.getName(), "category"), newImage);

                category.setImageUrl(fileUploaded.get(CloudinaryConstant.URL_PROPERTY));
                category.setImageId(fileUploaded.get(CloudinaryConstant.PUBLIC_ID));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        category.setName(body.getName());
        category.setStatus(body.getStatus());
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategoryById(String id) {
        CategoryEntity category = categoryRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));
        // FIXME: check chỗ này
        if (!category.getProductList().isEmpty()) {
            category.setDeleted(true);
            categoryRepository.save(category);
        } else {
            throw new CustomException(ErrorConstant.CANT_DELETE);
        }
    }


}
