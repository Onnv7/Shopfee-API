package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateProductRequest;
import com.hcmute.shopfee.dto.request.DeleteSomeProductRequest;
import com.hcmute.shopfee.dto.request.UpdateProductRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
import com.hcmute.shopfee.enums.ProductSortType;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = PRODUCT_CREATE_SUM)
    @PostMapping(path = POST_PRODUCT_CREATE_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<?>> createProduct(@ModelAttribute @Valid CreateProductRequest body, @RequestParam("type") ProductType productType) {
        productService.createProduct(body, body.getImage(), productType);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.CREATED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = PRODUCT_GET_BY_ID_SUM)
    @GetMapping(path = GET_PRODUCT_DETAILS_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetProductByIdResponse>> getProductDetailsById(@PathVariable(PRODUCT_ID) String id) {
        GetProductByIdResponse resData = productService.getProductDetailsById(id);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = PRODUCT_GET_VIEW_BY_ID_SUM)
    @GetMapping(path = GET_PRODUCT_VIEW_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetProductViewByIdResponse>> getProductViewById(@PathVariable(PRODUCT_ID) String id) {
        GetProductViewByIdResponse resData = productService.getProductViewById(id);
        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
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
            @RequestParam(name = "min_star", required = false, defaultValue = "0") @Min(value = 0, message = "Page must be greater than or equal 0") @Max(value = 5, message = "min_star must be lower than or equal 5") int minStar,

            @Parameter(name = "sort_type", required = false, example = "PRICE_DESC")
            @RequestParam(name = "sort_type", required = false) ProductSortType productSortType,

            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size
    ) {
        GetProductsByCategoryIdResponse products = productService.getProductsByCategoryId(categoryId, minPrice, maxPrice, minStar, productSortType, page, size);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(products)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = PRODUCT_GET_ALL_OR_SEARCH_ENABLED_SUM)
    @GetMapping(path = GET_PRODUCT_ALL_VISIBLE_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetAllVisibleProductResponse>> getAllProductsVisible(
            @Parameter(name = "key", description = "Key is name or description or id", required = false, example = "name or description")
            @RequestParam(name = "key", required = false) String key,

            @Parameter(name = "min_price", required = false, example = "1")
            @RequestParam(name = "min_price", required = false) @Min(value = 1, message = "min_price must be greater than 0") Long minPrice,

            @Parameter(name = "max_price", required = false, example = "1")
            @RequestParam(name = "max_price", required = false) @Min(value = 1, message = "max_price must be greater than 0") Long maxPrice,

            @Parameter(name = "min_star", required = false, example = "0")
            @RequestParam(name = "min_star", required = false, defaultValue = "0") @Min(value = 0, message = "Page must be greater than or equal 0") @Max(value = 5, message = "min_star must be lower than or equal 5") int minStar,

            @Parameter(name = "sort_type", required = false, example = "PRICE_DESC")
            @RequestParam(name = "sort_type", required = false) ProductSortType productSortType,

            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size
    ) {
        GetAllVisibleProductResponse resData = productService.getVisibleProductList( minPrice, maxPrice, minStar, productSortType, page, size, key);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = PRODUCT_GET_ALL_SUM)
    @GetMapping(path = GET_PRODUCT_ALL_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetProductListResponse>> getAllProducts(
            @Parameter(name = "key", description = "Key is name or description", required = false, example = "name or description")
            @RequestParam(name = "key", required = false) String key,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,
            @RequestParam(name = CATEGORY_ID, required = false) String categoryId,
            @RequestParam(name = "productStatus", required = false) ProductStatus productStatus
    ) {
        GetProductListResponse resData = productService.getProductList(key, page, size, categoryId, productStatus);
        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = PRODUCT_DELETE_BY_ID_SUM)
    @DeleteMapping(path = DELETE_PRODUCT_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> deleteProductById(@PathVariable("productId") String id) {
        productService.deleteProductById(id);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.DELETED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = PRODUCT_SOME_DELETE_BY_ID_SUM)
    @DeleteMapping(path = DELETE_SOME_PRODUCT_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> deleteSomeProductById(@RequestBody @Valid DeleteSomeProductRequest body) {
        productService.deleteSomeProductById(body.getProductIdList());

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.DELETED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = PRODUCT_UPDATE_BY_ID_SUM)
    @PutMapping(path = PUT_PRODUCT_UPDATE_BY_ID_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<?>> updateProductById(
            @ModelAttribute @Valid UpdateProductRequest body,
            @PathVariable("productId") String id
    ) {
        productService.updateProductById(body, id);
        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.UPDATED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = PRODUCT_GET_TOP_RATED_PRODUCTS_SUM)
    @GetMapping(path = GET_PRODUCT_TOP_RATED_PRODUCTS_SUB_PATH)
    public ResponseEntity<ResponseAPI<List<GetTopRatedProductResponse>>> getTopRatedProductQuantityOrder(
            @Parameter(name = "quantity", required = true, example = "10")
            @PathVariable("quantity") @Min(value = 1, message = "Page must be greater than 0") int itemQuantity
    ) {
        List<GetTopRatedProductResponse> resData = productService.getTopRatedProductQuantityOrder(itemQuantity);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
    @Operation(summary = PRODUCT_GET_TOP_SELLING_PRODUCTS_SUM)
    @GetMapping(path = GET_PRODUCT_TOP_SELLING_PRODUCTS_SUB_PATH)
    public ResponseEntity<ResponseAPI<List<GetTopSellingProductResponse>>> getTopSellingProductQuantityOrder(
            @Parameter(name = "quantity", required = true, example = "10")
            @PathVariable("quantity") @Min(value = 1, message = "Page must be greater than 0") int itemQuantity
    ) {
        List<GetTopSellingProductResponse> resData = productService.getTopSellingProductQuantityOrder(itemQuantity);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.GET)
                .timestamp(new Date())
                .data(resData)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = PRODUCT_POST_IMPORT_FILE_TO_CREATE_SUM)
    @PostMapping(path = POST_PRODUCT_CREATE_FROM_FILE_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    protected ResponseEntity<ResponseAPI<?>> createProductFromFile(@RequestParam("file") MultipartFile file, @RequestParam("product_type") ProductType productType) {
        if(productType == ProductType.BEVERAGE) {
            productService.createBeverageFromFile(file);
        } else if(productType == ProductType.CAKE) {
            productService.createCakeFromFile(file);
        }

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.CREATED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }
}
