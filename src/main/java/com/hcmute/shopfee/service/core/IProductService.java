package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.CreateProductRequest;
import com.hcmute.shopfee.dto.request.UpdateProductRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
import com.hcmute.shopfee.enums.ProductSortType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface IProductService {
    void createProduct(CreateProductRequest body, MultipartFile image, ProductType productType);
    GetProductByIdResponse getProductDetailsById(String id);
    GetProductViewByIdResponse getProductViewById(String id);
    GetProductsByCategoryIdResponse getProductsByCategoryId(String categoryId, Long minPrice, Long maxPrice, int minStar, ProductSortType productSortType, int page, int size);
    GetAllVisibleProductResponse getVisibleProductList(Long minPrice, Long maxPrice, int minStar, ProductSortType productSortType, int page, int size, String key);
    GetProductListResponse getProductList(String key, int page, int size, String categoryId, ProductStatus productStatus);
    void deleteProductById(String id);
    void deleteSomeProductById(List<String> productIdList);
    void updateProductById(UpdateProductRequest body, String id);
    List<GetTopRatedProductResponse> getTopRatedProductQuantityOrder(int quantity);
    List<GetTopSellingProductResponse> getTopSellingProductQuantityOrder(int quantity);
    void createBeverageFromFile(MultipartFile file);
    void createCakeFromFile(MultipartFile file);
}
