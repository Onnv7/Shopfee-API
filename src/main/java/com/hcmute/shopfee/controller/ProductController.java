package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.SecurityConstant;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.payload.request.CreateProductRequest;
import com.hcmute.shopfee.payload.request.DeleteSomeProductRequest;
import com.hcmute.shopfee.payload.request.UpdateProductRequest;
import com.hcmute.shopfee.enums.BranchProductStatus;
import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
import com.hcmute.shopfee.enums.param.ProductSortType;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.payload.response.*;
import com.hcmute.shopfee.service.core.IProductService;
import com.hcmute.shopfee.utils.HeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = PRODUCT_CONTROLLER_TITLE)
@RestController
@RequestMapping(PRODUCT_BASE_PATH)
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;


    @Operation(summary = PRODUCT_GET_AUTOCOMPLETE_SUM)
    @GetMapping(path = GET_PRODUCT_AUTOCOMPLETE_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> getAutocompleteTextList(@RequestParam("key") String key) {
        GetAutocompleteResponse data = productService.getAutocompleteTextList(key);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(data)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_CREATE_SUM)
    @PostMapping(path = POST_PRODUCT_CREATE_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<?>> createProduct(@ModelAttribute @Valid CreateProductRequest body, @RequestParam("product_type") ProductType productType) {
        productService.createProduct(body, body.getImage(), productType);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.CREATED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = PRODUCT_GET_BY_ID_SUM)
    @GetMapping(path = GET_PRODUCT_DETAILS_BY_ID_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN_MANAGER)
    public ResponseEntity<ResponseAPI<GetProductByIdResponse>> getProductDetailsById(@PathVariable(PRODUCT_ID) String id) {
        GetProductByIdResponse resData = productService.getProductDetailsById(id);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_GET_VIEW_BY_ID_SUM)
    @GetMapping(path = GET_PRODUCT_VIEW_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetProductViewByIdResponse>> getProductViewById(
            @PathVariable(PRODUCT_ID) String productId,
            @Parameter(name = "branch_id", required = false, example = "S001")
            @RequestParam(name = "branch_id", required = false) String branchId
    ) {
        GetProductViewByIdResponse resData = productService.getProductViewById(productId, branchId);
        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_GET_BY_CATEGORY_ID_SUM)
    @GetMapping(path = GET_PRODUCT_BY_CATEGORY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetProductsByCategoryIdResponse>> getProductByCategoryId(
            @PathVariable(CATEGORY_ID) String categoryId,
            @Parameter(name = "min_price", required = false, example = "1")
            @RequestParam(name = "min_price", required = false) @Min(value = 1, message = "min_price must be greater than 0") Long minPrice,

            @Parameter(name = "max_price", required = false, example = "1")
            @RequestParam(name = "max_price", required = false) @Min(value = 1, message = "max_price must be greater than 0") Long maxPrice,

            @Parameter(name = "min_star", required = false, example = "0")
            @RequestParam(name = "min_star", required = false, defaultValue = "0")
            @Min(value = 0, message = "Page must be greater than or equal 0")
            @Max(value = 5, message = "min_star must be lower than or equal 5") Integer minStar,

            @Parameter(name = "sort_type", required = false, example = "PRICE_DESC")
            @RequestParam(name = "sort_type", required = false) ProductSortType productSortType,

            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,

            @Parameter(name = "branch_id", required = false, example = "S001")
            @RequestParam(name = "branch_id", required = false) String branchId
    ) {
        GetProductsByCategoryIdResponse products = productService.getProductsByCategoryId(branchId, categoryId, minPrice, maxPrice, minStar, productSortType, page, size);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(products)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_GET_ALL_OR_SEARCH_ENABLED_SUM)
    @GetMapping(path = GET_PRODUCT_ALL_VISIBLE_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetProductCardListResponse>> getAllProductsVisible(
            @Parameter(name = "key", description = "Key is name or description or id", required = false, example = "P0001")
            @RequestParam(name = "key", required = false, defaultValue = "") String key,

            @Parameter(name = "min_price", required = false, example = "1")
            @RequestParam(name = "min_price", required = false) @Min(value = 1, message = "min_price must be greater than 0") Long minPrice,

            @Parameter(name = "max_price", required = false, example = "1")
            @RequestParam(name = "max_price", required = false) @Min(value = 1, message = "max_price must be greater than 0") Long maxPrice,

            @Parameter(name = "min_star", required = false, example = "0")
            @RequestParam(name = "min_star", required = false, defaultValue = "0")
            @Min(value = 0, message = "Page must be greater than or equal 0")
            @Max(value = 5, message = "min_star must be lower than or equal 5") Integer minStar,

            @Parameter(name = "sort_type", required = false, example = "PRICE_DESC")
            @RequestParam(name = "sort_type", required = false) ProductSortType productSortType,

            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,

            @Parameter(name = "branch_id", required = false, example = "S001")
            @RequestParam(name = "branch_id", required = false) String branchId
    ) {
        GetProductCardListResponse resData = productService.getVisibleProductList(branchId, minPrice, maxPrice, minStar, productSortType, page, size, key);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @Operation(summary = PRODUCT_GET_USER_TRACKING_SUM)
    @GetMapping(path = GET_PRODUCT_USER_TRACKING_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_USER)
    public ResponseEntity<ResponseAPI<List<GetUserProductTrackingCardResponse>>> getProductUserTracking(
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,
            @Parameter(name = "branch_id", required = false, example = "S001")
            @RequestParam(name = "branch_id", required = false) String branchId
    ) {
        List<GetUserProductTrackingCardResponse> resData = productService.getProductUserTracking(branchId, size);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @Operation(summary = PRODUCT_GET_ALL_SUM)
    @GetMapping(path = GET_PRODUCT_ALL_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN_MANAGER)
    public ResponseEntity<ResponseAPI<GetProductListResponse>> getProductList(
            @Parameter(name = "key", description = "Key is name or description", required = false, example = "")
            @RequestParam(name = "key", required = false, defaultValue = "") String key,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,
            @Parameter(name = "category_id", required = false, example = "C01")
            @RequestParam(name = "category_id", required = false) String categoryId,
            @Parameter(name = "product_status", required = false)
            @RequestParam(name = "product_status", required = false) ProductStatus productStatus,
            @Parameter(name = "branch_id", required = false, example = "S001")
            @RequestParam(name = "branch_id", required = false) String branchId,
            @Parameter(name = "branch_product_status", required = false)
            @RequestParam(name = "branch_product_status", required = false) BranchProductStatus branchProductStatus
    ) {
        GetProductListResponse resData = productService.getProductList(key, page, size, categoryId, productStatus, branchProductStatus, branchId);
        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_GET_BRANCH_LIST_SUM)
    @GetMapping(path = GET_PRODUCT_BRANCH_LIST_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<GetBranchProductListByProductResponse>> getBranchProductListByProduct(
            @PathVariable(PRODUCT_ID) String productId,
            @Parameter(name = "key", description = "Key is name or id of branch", required = false)
            @RequestParam(name = "key", required = false, defaultValue = "") String key,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,
            @RequestParam(name = "branch_product_status", required = false) BranchProductStatus branchProductStatus
    ) {
        GetBranchProductListByProductResponse resData = productService.getBranchProductListByProduct(productId, key, page, size, branchProductStatus);
        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_PATCH_CHANGE_BRANCH_PRODUCT_STATUS_SUM)
    @PatchMapping(path = PATCH_PRODUCT_BRANCH_CHANGE_STATUS_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN_MANAGER)
    public ResponseEntity<ResponseAPI<?>> updateBranchProductStatus(
            @PathVariable(PRODUCT_ID) String productId,
            @PathVariable(BRANCH_ID) String branchId,
            @Parameter(name = "branch_product_status", required = true, example = "UNAVAILABLE")
            @RequestParam(name = "branch_product_status", required = true) BranchProductStatus branchProductStatus
    ) {
        productService.updateBranchProductStatus(productId, branchId, branchProductStatus);
        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.UPDATED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @Operation(summary = PRODUCT_DELETE_BY_ID_SUM)
    @DeleteMapping(path = DELETE_PRODUCT_BY_ID_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<?>> deleteProductById(@PathVariable("productId") String id) {
        productService.deleteProductById(id);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.DELETED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_SOME_DELETE_BY_ID_SUM)
    @DeleteMapping(path = DELETE_SOME_PRODUCT_BY_ID_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<?>> deleteSomeProductById(@RequestBody @Valid DeleteSomeProductRequest body) {
        productService.deleteSomeProductById(body.getProductIdList());

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.DELETED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_UPDATE_BY_ID_SUM)
    @PutMapping(path = PUT_PRODUCT_UPDATE_BY_ID_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<?>> updateProductById(
            @ModelAttribute @Valid UpdateProductRequest body,
            @PathVariable("productId") String id,
            @RequestParam("product_type") ProductType productType
    ) {
        productService.updateProductById(body, id, productType);
        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.UPDATED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_GET_TOP_RATED_PRODUCTS_SUM)
    @GetMapping(path = GET_PRODUCT_TOP_RATED_PRODUCTS_SUB_PATH)
    public ResponseEntity<ResponseAPI<List<GetTopRatedProductResponse>>> getTopRatedProductQuantityOrder(
            @Parameter(name = "quantity", required = true, example = "10")
            @PathVariable("quantity") @Min(value = 1, message = "Page must be greater than 0") int itemQuantity,

            @Parameter(name = "branch_id", required = false, example = "S001")
            @RequestParam(name = "branch_id", required = false) String branchId
    ) {
        List<GetTopRatedProductResponse> resData = productService.getTopRatedProductQuantityOrder(itemQuantity, branchId);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_GET_TOP_SELLING_PRODUCTS_SUM)
    @GetMapping(path = GET_PRODUCT_TOP_SELLING_PRODUCTS_SUB_PATH)
    public ResponseEntity<ResponseAPI<List<GetTopSellingProductResponse>>> getTopSellingProductQuantityOrder(
            @Parameter(name = "quantity", required = true, example = "10")
            @PathVariable("quantity") @Min(value = 1, message = "Page must be greater than 0") int itemQuantity,

            @Parameter(name = "branch_id", required = false, example = "S001")
            @RequestParam(name = "branch_id", required = false) String branchId
    ) {
        List<GetTopSellingProductResponse> resData = productService.getTopSellingProductQuantityOrder(itemQuantity, branchId);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_IMPORT_FILE_TO_CREATE_SUM)
    @PostMapping(path = POST_PRODUCT_CREATE_FROM_FILE_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    protected ResponseEntity<ResponseAPI<List<CreateProductFromFileErrorResponse>>> createProductFromFile(@RequestParam("file") MultipartFile file
            , @RequestParam("product_type") ProductType productType, @RequestParam("force") boolean force) {
        List<CreateProductFromFileErrorResponse> data = new ArrayList<>();
        if (productType == ProductType.BEVERAGE) {
            data = productService.createBeverageFromFile(file, force);
        } else if (productType == ProductType.CAKE) {
            data = productService.createCakeFromFile(file, force);
        }

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.CREATED)
                .timestamp(new Date())
                .data(data)
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = PRODUCT_CHECK_EXISTED_NAME_SUM)
    @GetMapping(path = GET_PRODUCT_CHECK_NAME_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    protected ResponseEntity<ResponseAPI<CheckExistedNameResponse>> isExistedProductName(@RequestParam("product_name") String productName) {
        CheckExistedNameResponse data = productService.isExistedProductName(productName);
        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(data)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = PRODUCT_DOWNLOAD_IMPORT_FILE_SUM)
    @GetMapping(path = GET_PRODUCT_DOWNLOAD_IMPORT_FILE_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    protected ResponseEntity<?> downloadImportFile(@RequestParam("product_type") ProductType productType) {
        byte[] data = {};
        String fileName = "beverage.xlsx";
        if (productType == ProductType.BEVERAGE) {
            data = productService.downloadImportBeverageFile();
            fileName = "beverage.xlsx";
        } else if (productType == ProductType.CAKE) {
            data = productService.downloadImportCakeFile();
            fileName = "cake.xlsx";
        }
        HttpHeaders headers = HeaderUtils.setAttachFile(fileName);


        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
