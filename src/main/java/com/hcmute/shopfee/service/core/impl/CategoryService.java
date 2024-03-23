package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.CreateCategoryRequest;
import com.hcmute.shopfee.dto.request.UpdateCategoryRequest;
import com.hcmute.shopfee.dto.response.GetCategoryByIdResponse;
import com.hcmute.shopfee.dto.response.GetCategoryListResponse;
import com.hcmute.shopfee.dto.response.GetVisibleCategoryListResponse;
import com.hcmute.shopfee.entity.sql.database.AlbumEntity;
import com.hcmute.shopfee.entity.sql.database.CategoryEntity;
import com.hcmute.shopfee.enums.AlbumType;
import com.hcmute.shopfee.enums.CategoryStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.AlbumRepository;
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
    private final AlbumRepository albumRepository;

    @Override
    public void createCategory(CreateCategoryRequest body) {
        if(!ImageUtils.isValidImageFile(body.getImage())) {
            throw new CustomException(ErrorConstant.IMAGE_INVALID);
        }

        String cgrName = body.getName();
        CategoryEntity existedCategory = categoryRepository.findByName(cgrName).orElse(null);
        if (existedCategory != null) {
            throw new CustomException(ErrorConstant.EXISTED_DATA, "Category already exists");
        }
        byte[] originalImage;
        try {
            originalImage = body.getImage().getBytes();
            byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
            HashMap<String, String> fileUploaded = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CATEGORY_PATH,
                    StringUtils.generateFileName(body.getName(), "category"), newImage);
            AlbumEntity image = AlbumEntity.builder()
                    .type(AlbumType.CATEGORY)
                    .cloudinaryImageId(fileUploaded.get(CloudinaryConstant.PUBLIC_ID))
                    .imageUrl(fileUploaded.get(CloudinaryConstant.URL_PROPERTY))
                    .build();
            CategoryEntity category = CategoryEntity.builder()
                    .image(image)
                    .name(body.getName())
                    .status(body.getStatus())
                    .build();
            categoryRepository.save(category);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GetCategoryByIdResponse getCategoryById(String id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.CATEGORY_ID_NOT_FOUND + id));
        return GetCategoryByIdResponse.fromCategoryEntity(category);
    }

    @Override
    public List<GetCategoryListResponse> getCategoryList() {
        List<CategoryEntity> categoryList = categoryRepository.findAll();
        return GetCategoryListResponse.fromCategoryEntityList(categoryList);
    }

    @Override
    public List<GetVisibleCategoryListResponse> getVisibleCategoryList() {
        List<CategoryEntity> categoryList = categoryRepository.findByStatus(CategoryStatus.VISIBLE);
        return GetVisibleCategoryListResponse.fromCategoryEntityList(categoryList);
    }

    @Override
    public void updateCategory(UpdateCategoryRequest body, String id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.CATEGORY_ID_NOT_FOUND + id));
        if (body.getImage() != null) {
            try {
                byte[] originalImage = body.getImage().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);

                HashMap<String, String> fileUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CATEGORY_PATH,
                        StringUtils.generateFileName(body.getName(), "category"),
                        newImage
                );
                AlbumEntity image = AlbumEntity.builder()
                        .type(AlbumType.CATEGORY)
                        .cloudinaryImageId(fileUploaded.get(CloudinaryConstant.PUBLIC_ID))
                        .imageUrl(fileUploaded.get(CloudinaryConstant.URL_PROPERTY))
                        .build();
                category.setImage(image);
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
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.CATEGORY_ID_NOT_FOUND + id));

        if (category.getProductList().isEmpty()) {
            categoryRepository.delete(category);
            albumRepository.delete(category.getImage());
        } else {
            throw new CustomException(ErrorConstant.CANT_DELETE);
        }
    }


}
