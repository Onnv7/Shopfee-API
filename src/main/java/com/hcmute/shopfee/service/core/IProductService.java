package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.payload.request.CreateProductRequest;
import com.hcmute.shopfee.payload.request.UpdateProductRequest;
import com.hcmute.shopfee.enums.BranchProductStatus;
import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
import com.hcmute.shopfee.enums.param.ProductSortType;
import com.hcmute.shopfee.payload.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface IProductService {
    void createProduct(CreateProductRequest body, MultipartFile image, ProductType productType);
    GetAutocompleteResponse getAutocompleteTextList(String key);
    GetProductByIdResponse getProductDetailsById(String id);
    GetProductViewByIdResponse getProductViewById(String productId, String branchId);
    GetProductsByCategoryIdResponse getProductsByCategoryId(String branchId, String categoryId, Long minPrice, Long maxPrice, Integer minStar, ProductSortType productSortType, int page, int size);
    GetProductCardListResponse getVisibleProductList(String branchId, Long minPrice, Long maxPrice, Integer minStar, ProductSortType productSortType, int page, int size, String key);
    List<GetUserProductTrackingCardResponse> getProductUserTracking(String branchId, Integer size);
    GetProductListResponse getProductList(String key, int page, int size, String categoryId, ProductStatus productStatus, BranchProductStatus branchProductStatus, String branchId);
    GetBranchProductListByProductResponse getBranchProductListByProduct(String productId, String key, int page, int size, BranchProductStatus branchProductStatus);
    void updateBranchProductStatus(String productId, String branchId, BranchProductStatus branchProductStatus);
    void deleteProductById(String id);
    void deleteSomeProductById(List<String> productIdList);
    void updateProductById(UpdateProductRequest body, String id, ProductType productType);
    List<GetTopRatedProductResponse> getTopRatedProductQuantityOrder(int quantity, String branchId);
    List<GetTopSellingProductResponse> getTopSellingProductQuantityOrder(int quantity, String branchId);
    List<CreateProductFromFileErrorResponse> createBeverageFromFile(MultipartFile file, boolean force);
    List<CreateProductFromFileErrorResponse>  createCakeFromFile(MultipartFile file, boolean force);
    CheckExistedNameResponse isExistedProductName(String productName);
    byte[] downloadImportBeverageFile();
    byte[] downloadImportCakeFile();
}
