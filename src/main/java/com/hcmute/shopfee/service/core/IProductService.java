package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.CreateProductRequest;
import com.hcmute.shopfee.dto.request.UpdateProductRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface IProductService {
    void createProduct(CreateProductRequest body, MultipartFile image, ProductType productType);
    GetProductByIdResponse getProductDetailsById(String id);
    GetProductEnabledByIdResponse getProductEnabledById(String id);
    List<GetProductsByCategoryIdResponse> getProductsByCategoryId(String categoryId);
    List<GetAllVisibleProductResponse> getVisibleProductList(int page, int size, String key);
    GetProductListResponse getProductList(String key, int page, int size, String categoryId, ProductStatus productStatus);
    void deleteProductById(String id);
    void deleteSomeProductById(List<String> productIdList);
    void updateProductById(UpdateProductRequest body, String id);
    List<GetTopProductResponse> getTopProductQuantityOrder(int quantity);
    void createProductFromFile(MultipartFile file) throws IOException;
}
