package com.hcmute.shopfee.service.core.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.CloudinaryUploadResponse;
import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.request.CreateProductRequest;
import com.hcmute.shopfee.dto.request.UpdateProductRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.sql.database.AlbumEntity;
import com.hcmute.shopfee.entity.sql.database.CategoryEntity;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.entity.sql.database.product.SizeEntity;
import com.hcmute.shopfee.entity.sql.database.product.ToppingEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import com.hcmute.shopfee.repository.database.AlbumRepository;
import com.hcmute.shopfee.repository.database.CategoryRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.repository.database.review.ProductReviewRepository;
import com.hcmute.shopfee.service.core.IProductService;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.elasticsearch.ProductSearchService;
import com.hcmute.shopfee.service.redis.ProductRedisService;
import com.hcmute.shopfee.utils.ExcelUtils;
import com.hcmute.shopfee.utils.MediaUtils;
import com.hcmute.shopfee.utils.RegexUtils;
import com.hcmute.shopfee.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ModelMapperService modelMapperService;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductSearchService productSearchService;
    private final ProductReviewRepository productReviewRepository;
    private final AlbumRepository albumRepository;
    private final ProductRedisService productRedisService;


    public static long getMinPrice(List<SizeEntity> sizeList) {
        long min = sizeList.get(0).getPrice();
        for (SizeEntity item : sizeList) {
            if (min > item.getPrice()) {
                min = item.getPrice();
            }
        }
        return min;
    }

    private boolean checkProductData(List<String> listName) {
        Set<String> uniqueNames = new HashSet<>(listName);
        if (uniqueNames.size() < listName.size()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void createProduct(CreateProductRequest body, MultipartFile image, ProductType productType) {
        if (!MediaUtils.isValidImageFile(body.getImage())) {
            throw new ShopfeeException(ShopfeeErrorCode.IMAGE_INVALID);
        }
        if ((productType == ProductType.BEVERAGE && (body.getSizeList() == null || body.getPrice() != null))) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Beverage need size and not price");
        } else if (productType == ProductType.CAKE) {
            if (body.getToppingList() != null || body.getSizeList() != null || body.getPrice() == null) {
                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Cakes do not need toppings or size, and need a price");
            }
        }
        if (productRepository.findByName(body.getName().trim()).orElse(null) != null) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.EXISTED_DATA, "Product named \"" + body.getName().trim() + "\" already exists");
        }

        ProductEntity productEntity = modelMapperService.mapClass(body, ProductEntity.class);
        if (body.getToppingList() != null) {
            List<ToppingEntity> toppingList = ToppingEntity.fromToppingDtoList(body.getToppingList(), productEntity);
            if (!checkProductData(toppingList.stream().map(it -> it.getName().trim()).toList())) {
                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "The topping's name list is duplicated");
            }
            productEntity.setToppingList(toppingList);
        }

        if (body.getSizeList() != null) {
            List<SizeEntity> sizeList = SizeEntity.fromToppingDtoList(body.getSizeList(), productEntity);
            if (!checkProductData(sizeList.stream().map(it -> it.getSize().name()).toList())) {
                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "The size's name list is duplicated");
            }
            productEntity.setSizeList(sizeList);
        }
        productEntity.setType(productType);
        productEntity.setName(body.getName().trim());
        productEntity.setDescription(body.getDescription());
        productEntity.setStatus(body.getStatus());

        if (productType == ProductType.CAKE) {
            productEntity.setPrice(body.getPrice());
        } else if (productType == ProductType.BEVERAGE) {
            productEntity.setPrice(getMinPrice(productEntity.getSizeList()));
        }
        byte[] originalImage = new byte[0];
        try {
            originalImage = image.getBytes();
            byte[] newImage = MediaUtils.resizeImage(originalImage, 200, 200);

            CategoryEntity categoryEntity = categoryRepository.findById(body.getCategoryId())
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.CATEGORY_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + body.getCategoryId().trim()));

            productEntity.setCategory(categoryEntity);
            CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                    CloudinaryConstant.PRODUCT_PATH,
                    StringUtils.generateFileName(body.getName().trim(), "product"),
                    newImage
            );

            AlbumEntity productImage = AlbumEntity.builder()
                    .imageUrl(imageUploaded.getUrl())
                    .type(AlbumType.PRODUCT)
                    .cloudinaryImageId(imageUploaded.getPublicId())
                    .thumbnailUrl(cloudinaryService.getThumbnailUrlOfImage(imageUploaded.getPublicId()))
                    .build();
            productEntity.setImage(productImage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        productRepository.save(productEntity);
    }

    @Override
    public GetProductByIdResponse getProductDetailsById(String id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + id));
        GetProductByIdResponse result = modelMapperService.mapClass(product, GetProductByIdResponse.class);
        result.setImageUrl(product.getImage().getImageUrl());
        result.setCategoryId(product.getCategory().getId());
        if (product.getType() == ProductType.CAKE) {
            result.setPrice(product.getPrice());
        }
        return result;
    }

    @Override
    public GetProductViewByIdResponse getProductViewById(String id) {

        try {
            GetProductViewByIdResponse dataCache = productRedisService.getProductView(id);
            if(dataCache != null && dataCache.getStatus() != ProductStatus.HIDDEN) {
                return dataCache;
            }
        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
        }

        ProductEntity product = productRepository.findByIdAndStatusNot(id, ProductStatus.HIDDEN)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + id));
        GetProductViewByIdResponse data = modelMapperService.mapClass(product, GetProductViewByIdResponse.class);

        data.setImageUrl(product.getImage().getImageUrl());

        RatingSummaryQueryDto ratingSummaryQueryDto = productReviewRepository.getRatingSummary(product.getId());
        data.setRatingSummary(RatingSummaryDto.fromRatingSummaryDto(ratingSummaryQueryDto));

        try {
            productRedisService.saveProductView(data);
        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public GetProductsByCategoryIdResponse getProductsByCategoryId(String categoryId, Long minPrice, Long maxPrice, int minStar, ProductSortType productSortType, int page, int size) {
        GetProductsByCategoryIdResponse data = new GetProductsByCategoryIdResponse();
        List<GetProductsByCategoryIdResponse.ProductCard> productList = new ArrayList<>();

        data.setProductList(productList);
        // TODO: nên check category not hidden
        Page<ProductEntity> productPage = null;

        Pageable pageable = PageRequest.of(page - 1, size);
        if (minPrice != null && maxPrice != null) {
            if (productSortType == ProductSortType.PRICE_DESC) {
                pageable = PageRequest.of(page - 1, size, Sort.by("price").descending());
            } else if (productSortType == ProductSortType.PRICE_ASC) {
                pageable = PageRequest.of(page - 1, size, Sort.by("price").ascending());
            }
            productPage = productRepository.getProductByCategoryIdAndFilter(categoryId, minPrice, maxPrice, minStar, pageable);

        } else {
            productPage = productRepository.findByCategory_IdAndStatusNot(categoryId, ProductStatus.HIDDEN, PageRequest.of(page - 1, size));
        }
        data.setTotalPage(productPage.getTotalPages());

        List<ProductEntity> productEntityList = productPage.getContent();
        for (ProductEntity entity : productEntityList) {
            RatingSummaryQueryDto ratingSummary = productReviewRepository.getRatingSummary(entity.getId());
            productList.add(GetProductsByCategoryIdResponse.ProductCard.fromProductEntity(entity, ratingSummary));
        }
        return data;
    }

    @Override
    public GetAllVisibleProductResponse getVisibleProductList(Long minPrice, Long maxPrice, int minStar, ProductSortType productSortType, int page, int size, String key) {
        GetAllVisibleProductResponse data = new GetAllVisibleProductResponse();

        List<GetAllVisibleProductResponse.ProductCard> productList = new ArrayList<>();

        PageRequest pageable = PageRequest.of(page - 1, size);
        try {
            data = productRedisService.getProductVisibleList(key, pageable, minPrice, maxPrice, minStar);
            if(data != null) {
                return data;
            } else {
                data = new GetAllVisibleProductResponse();
            }
        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
        }


        if (!key.isBlank()) {
            Page<ProductIndex> productIndexPage = productSearchService.searchVisibleProduct(key, pageable);
            data.setTotalPage(productIndexPage.getTotalPages());
            List<ProductIndex> productIndexList = productIndexPage.getContent();
            for (ProductIndex index : productIndexList) {
                RatingSummaryQueryDto ratingSummaryQueryDto = productReviewRepository.getRatingSummary(index.getId());
                productList.add(GetAllVisibleProductResponse.ProductCard.fromProductIndex(index, ratingSummaryQueryDto));
            }
        }
        else {
            Page<ProductEntity> productPage = null;
            if (minPrice != null && maxPrice != null) {
                if (productSortType == ProductSortType.PRICE_DESC) {
                    pageable = PageRequest.of(page - 1, size, Sort.by("price").descending());
                } else if (productSortType == ProductSortType.PRICE_ASC) {
                    pageable = PageRequest.of(page - 1, size, Sort.by("price").ascending());
                }

                productPage = productRepository.getAllProductAndFilter(minPrice, maxPrice, minStar, pageable);
            } else {
                productPage = productRepository.findByStatusNot(ProductStatus.HIDDEN, pageable);

            }
            data.setTotalPage(productPage.getTotalPages());
            List<ProductEntity> productEntityList = productPage.getContent();
            for (ProductEntity entity : productEntityList) {
                RatingSummaryQueryDto ratingSummaryQueryDto = productReviewRepository.getRatingSummary(entity.getId());
                productList.add(GetAllVisibleProductResponse.ProductCard.fromProductEntity(entity, ratingSummaryQueryDto));
            }
        }
        data.setProductList(productList);
        try {
            productRedisService.saveProductVisibleList(data, key, pageable, minPrice, maxPrice, minStar);
        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public GetProductListResponse getProductList(String key, int page, int size, String categoryId, ProductStatus productStatus) {
        Pageable pageable = PageRequest.of(page - 1, size);
        String categoryIdRegex = RegexUtils.generateFilterRegexString(categoryId != null ? categoryId : "");
        String productStatusRegex = RegexUtils.generateFilterRegexString(productStatus != null ? productStatus.toString() : "");
        if (key.isBlank()) {
            Page<ProductEntity> productPage = productRepository.getProductList(categoryIdRegex, productStatusRegex, pageable);
            GetProductListResponse productList = new GetProductListResponse();
            productList.setTotalPage(productPage.getTotalPages());
            productList.setProductList(GetProductListResponse.fromProductEntityList(productPage.getContent()));
            return productList;
        } else {
            Page<ProductIndex> productPage = productSearchService.searchProduct(key, categoryIdRegex, productStatusRegex, page, size);
            GetProductListResponse resultPage = new GetProductListResponse();
            resultPage.setTotalPage(productPage.getTotalPages());
            resultPage.setProductList(modelMapperService.mapList(productPage.getContent(), GetProductListResponse.Product.class));
            return resultPage;
        }
    }

    @Override
    public void deleteProductById(String id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + id));

        if (productRepository.countOrderItem(id) == 0) {
            productRepository.delete(product);
            if (product.getImage().getCloudinaryImageId() == null) {
                albumRepository.delete(product.getImage());
            }
        } else {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.CANT_DELETE);
        }
    }

    @Override
    public void deleteSomeProductById(List<String> productIdList) {
        int successCount = 0;
        for (String id : productIdList) {
            try {
                deleteProductById(id);
                successCount++;
            } catch (Exception e) {
                // TODO: xử lý e ở đây
            }
        }
    }

    @Transactional
    @Override
    public void updateProductById(UpdateProductRequest body, String id, ProductType productType) {
        if ((productType == ProductType.BEVERAGE && (body.getSizeList() == null || body.getPrice() != null))) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Beverage need size and not price");
        } else if (productType == ProductType.CAKE) {
            if (body.getToppingList() != null || body.getSizeList() != null || body.getPrice() == null) {
                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Cakes do not need toppings or size, and need a price");
            }
        }

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + id));


        modelMapperService.map(body, product);

        if (body.getImage() != null) {
            try {
                byte[] originalImage = body.getImage().getBytes();

                byte[] newImage = MediaUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse fileUploaded = cloudinaryService.uploadFileToFolder(CloudinaryConstant.PRODUCT_PATH,
                        StringUtils.generateFileName(body.getName(), "product"), newImage);

                AlbumEntity productImage = AlbumEntity.builder()
                        .type(AlbumType.PRODUCT)
                        .imageUrl(fileUploaded.getUrl())
                        .cloudinaryImageId(fileUploaded.getPublicId())
                        .thumbnailUrl(cloudinaryService.getThumbnailUrlOfImage(fileUploaded.getPublicId()))
                        .build();
                product.setImage(productImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (productType == ProductType.CAKE) {
            product.setPrice(body.getPrice());
        } else {
            product.setPrice(getMinPrice(product.getSizeList()));
        }

        CategoryEntity category = categoryRepository.findById(body.getCategoryId())
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.CATEGORY_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + id));
        product.setCategory(category);

        productRepository.save(product);
    }

    @Override
    public List<GetTopRatedProductResponse> getTopRatedProductQuantityOrder(int quantity) {
        List<GetTopRatedProductResponse> data = new ArrayList<>();

        List<ProductEntity> productEntityList = productRepository.getTopRatingProduct(quantity);
        for (ProductEntity entity : productEntityList) {
            RatingSummaryQueryDto ratingSummaryQueryDto = productReviewRepository.getRatingSummary(entity.getId());
            data.add(GetTopRatedProductResponse.fromProductEntity(entity, ratingSummaryQueryDto));
        }

        return data;
    }

    @Override
    public List<GetTopSellingProductResponse> getTopSellingProductQuantityOrder(int quantity) {
        List<GetTopSellingProductResponse> data = new ArrayList<>();

        List<ProductEntity> productEntityList = productRepository.getTopProductBySoldQuantity(quantity);
        if (productEntityList.size() < quantity) {
            List<String> productIdList = productEntityList.stream().map(ProductEntity::getId).toList();
            List<ProductEntity> productMore = productRepository.getProductWithIdNotIn(productIdList, quantity - productEntityList.size());
            productEntityList.addAll(productMore);
        }
        for (ProductEntity entity : productEntityList) {
            RatingSummaryQueryDto ratingSummaryQueryDto = productReviewRepository.getRatingSummary(entity.getId());
            data.add(GetTopSellingProductResponse.fromProductEntity(entity, ratingSummaryQueryDto));
        }

        return data;
    }


    @Transactional
    @Override
    public List<CreateProductFromFileErrorResponse> createBeverageFromFile(MultipartFile file, boolean force) {
        List<CreateProductFromFileErrorResponse> data = new ArrayList<>();
        boolean hasError = false;
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            ProductEntity product = new ProductEntity();
            List<SizeEntity> sizeEntityList = new ArrayList<>();
            product.setSizeList(sizeEntityList);
            List<ToppingEntity> toppingEntityList = new ArrayList<>();
            product.setToppingList(toppingEntityList);
            boolean isNewProductValueState = false;
            List<ProductEntity> productValidList = new ArrayList<>();

            boolean dataValidFlag = false;

            // đọc từng hàng
            for (Row row : sheet) {
                boolean skip4Col = false;
                CreateProductFromFileErrorResponse errorRow = new CreateProductFromFileErrorResponse();
                List<CreateProductFromFileErrorResponse.CellDataError> errorColList = new ArrayList<>();
                int rowIndex = row.getRowNum();
                errorRow.setRowIndex(rowIndex);
                if (rowIndex == 0 || ExcelUtils.haveAnyOneCellWithData(row, 0, 8) == false) {
                    continue;
                }


                SizeEntity sizeEntity = new SizeEntity();
                ToppingEntity toppingEntity = new ToppingEntity();

                if (ExcelUtils.haveAnyOneCellWithData(row, 0, 4)) {
                    isNewProductValueState = true;
                } else {
                    isNewProductValueState = false;
                }
                if (isNewProductValueState) {
                    // row = 1 la dac biet vi isNewProductValueState = true nhung khong the them moi dc
                    if (rowIndex != 1) {
                        if ((product.getSizeList() == null || product.getSizeList().isEmpty())) {
                            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Must have at least 1 size");
                        } else if (dataValidFlag) {
                            product.setPrice(getMinPrice(product.getSizeList()));
                            productValidList.add(product);
                            System.out.println("Saving product " + product.toString());
                        }
                    }

                    dataValidFlag = true;
                    product = new ProductEntity();
                    product.setType(ProductType.BEVERAGE);
                    sizeEntityList = new ArrayList<>();
                    product.setSizeList(sizeEntityList);
                    toppingEntityList = new ArrayList<>();
                    product.setToppingList(toppingEntityList);

                    List<Integer> colIndexList = ExcelUtils.getColIndexIsNoDataList(row, 0, 4);
                    if (!colIndexList.isEmpty()) {
                        skip4Col = true;
                        for (Integer colIndex : colIndexList) {
                            if (colIndex != null) {
                                CreateProductFromFileErrorResponse.CellDataError errorCol = new CreateProductFromFileErrorResponse.CellDataError();
                                dataValidFlag = false;
                                hasError = true;
                                errorCol.setErrorCode(8);
                                errorCol.setColIndex(colIndex);
                                errorColList.add(errorCol);
                            }
                        }
                    }

                }

                // đọc từng ô
                for (Cell cell : row) {
                    int colIndex = cell.getColumnIndex();
                    CreateProductFromFileErrorResponse.CellDataError errorCol = new CreateProductFromFileErrorResponse.CellDataError();
                    errorCol.setColIndex(colIndex);

                    // van con dang trong san pham cu
                    if ((colIndex <= 4 && !isNewProductValueState) || skip4Col) {
                        continue;
                    }

                    switch (colIndex) {
                        case 0:
                            ProductEntity existedProduct = productRepository.findByName(cell.getStringCellValue().trim()).orElse(null);
                            if (existedProduct != null) {
                                dataValidFlag = false;
                                hasError = true;
                                errorCol.setErrorCode(1);
                                break;
                            }
                            List<String> productName = new ArrayList<>(productValidList.stream().map(ProductEntity::getName).toList());

                            productName.add(cell.getStringCellValue().trim());
                            if (!checkProductData(productName)) {
                                dataValidFlag = false;
                                hasError = true;
                                errorCol.setErrorCode(2);
                                break;
                            }
                            product.setName(cell.getStringCellValue().trim());
                            break;
                        case 1:
                            CategoryEntity category = categoryRepository.findByName(cell.getStringCellValue().trim()).orElse(null);
                            if (category == null) {
                                dataValidFlag = false;
                                hasError = true;
                                errorCol.setErrorCode(3);
                                break;
                            }
                            product.setCategory(category);
                            break;
                        case 2:
                            product.setStatus(ProductStatus.valueOf(cell.getStringCellValue().trim()));
                            break;
                        case 3:
                            product.setDescription(cell.getStringCellValue().trim());
                            break;
                        case 4:
                            String imageUrl = cell.getStringCellValue().trim();
                            AlbumEntity image = albumRepository.findByCloudinaryImageIdIsNotNullAndImageUrl(imageUrl.trim()).orElse(null);
                            if (image != null) {
                                if (image.getProduct() != null || image.getCategory() != null) {
                                    dataValidFlag = false;
                                    hasError = true;
                                    errorCol.setErrorCode(4);
                                    break;
                                }
                                product.setImage(image);
                            } else {
                                AlbumEntity newImage = AlbumEntity.builder()
                                        .product(product)
                                        .type(AlbumType.PRODUCT)
                                        .thumbnailUrl(imageUrl)
                                        .imageUrl(imageUrl)
                                        .build();
                                product.setImage(newImage);
                            }
                            break;
                        case 5:
                            if (ExcelUtils.isNoDataCell(cell)) {
                                continue;
                            }
                            if (product.getSizeList() != null && !product.getSizeList().isEmpty()) {
                                List<String> sizeNameList = new ArrayList<>(product.getSizeList().stream().map(it -> it.getSize().name()).toList());
                                sizeNameList.add(cell.getStringCellValue().trim());
                                if (!checkProductData(sizeNameList)) {
                                    dataValidFlag = false;
                                    hasError = true;
                                    errorCol.setErrorCode(5);
                                    break;
                                }
                            }

                            sizeEntity.setSize(ProductSize.valueOf(cell.getStringCellValue().trim()));
                            break;
                        case 6:
                            if (!ExcelUtils.isNoDataCell(cell) && (long) cell.getNumericCellValue() < 1000) {
                                dataValidFlag = false;
                                hasError = true;
                                errorCol.setErrorCode(6);
                                break;
                            }

                            if (sizeEntity.getSize() == null) {
                                continue;
                            }
                            sizeEntity.setPrice((long) cell.getNumericCellValue());

                            SizeEntity newEntity = new SizeEntity();
                            newEntity.setSize(sizeEntity.getSize());
                            newEntity.setPrice(sizeEntity.getPrice());
                            newEntity.setProduct(product);
                            sizeEntityList.add(newEntity);
                            break;
                        case 7:
                            if (ExcelUtils.isNoDataCell(cell)) {
                                continue;
                            }
                            if (product.getToppingList() != null && !product.getToppingList().isEmpty()) {
                                List<String> toppingNameList = new ArrayList<>(product.getToppingList().stream().map(it -> it.getName()).toList());
                                toppingNameList.add(cell.getStringCellValue().trim());
                                if (!checkProductData(toppingNameList)) {
                                    dataValidFlag = false;
                                    hasError = true;
                                    errorCol.setErrorCode(7);
                                    break;
                                }
                            }

                            toppingEntity.setName(cell.getStringCellValue().trim());
                            break;
                        case 8:
                            if (toppingEntity.getName() == null) {
                                continue;
                            }

                            if (!ExcelUtils.isNoDataCell(cell) && (long) cell.getNumericCellValue() < 1000) {
                                dataValidFlag = false;
                                hasError = true;
                                errorCol.setErrorCode(6);
                                break;
                            }

                            toppingEntity.setPrice((long) cell.getNumericCellValue());
                            ToppingEntity newToppingEntity = new ToppingEntity();
                            newToppingEntity.setProduct(product);
                            newToppingEntity.setName(toppingEntity.getName());
                            newToppingEntity.setPrice(toppingEntity.getPrice());
                            toppingEntityList.add(newToppingEntity);
                            break;
                    }

                    if (errorCol.getErrorCode() != null) {
                        errorColList.add(errorCol);
                    }
                }
                if (!errorColList.isEmpty()) {
                    errorRow.setErrorList(errorColList);
                    data.add(errorRow);
                }
                System.out.println(product);
            }

            if ((product.getSizeList() == null || product.getSizeList().isEmpty())) {
                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Must have at least 1 size");
            } else if (dataValidFlag) {
                product.setPrice(getMinPrice(product.getSizeList()));
                productValidList.add(product);
            }

            if (force) {
                productRepository.saveAll(productValidList);
//                productSearchService.createAllProduct(productValidList);
            } else {
                if (!hasError) {
                    productRepository.saveAll(productValidList);
//                    productSearchService.createAllProduct(productValidList);
                }
            }
            inputStream.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "The data is not in the correct format");
        } catch (ShopfeeException e) {
            throw new ShopfeeException(e.getError(), e.getDevMessage());
        } catch (Exception e) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.SERVER_ERROR, e.getMessage());
        }

        return data;
    }


    @Transactional
    @Override
    public List<CreateProductFromFileErrorResponse> createCakeFromFile(MultipartFile file, boolean force) {
        int success = 0;
        List<CreateProductFromFileErrorResponse> data = new ArrayList<>();
        List<ProductEntity> productValidList = new ArrayList<>();
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            boolean hasError = false;
            // đọc từng hàng
            for (Row row : sheet) {
                boolean rowValid = true;
                int rowIndex = row.getRowNum();
                if(rowIndex == 0) {
                    continue;
                }
                CreateProductFromFileErrorResponse errorRow = new CreateProductFromFileErrorResponse();
                List<CreateProductFromFileErrorResponse.CellDataError> errorColList = new ArrayList<>();
                errorRow.setErrorList(errorColList);
                errorRow.setRowIndex(rowIndex);

                ProductEntity product = new ProductEntity();
                product.setType(ProductType.CAKE);
                List<Integer> colIndexList = ExcelUtils.getColIndexIsNoDataList(row, 0, 5);
                if (!colIndexList.isEmpty()) {
                    for (Integer index : colIndexList) {
                        if (index != null) {
                            CreateProductFromFileErrorResponse.CellDataError errorCol = new CreateProductFromFileErrorResponse.CellDataError();
                            hasError = true;
                            rowValid = false;
                            errorCol.setErrorCode(8);
                            errorCol.setColIndex(index);
                            errorColList.add(errorCol);
                        }
                    }
                    data.add(errorRow);
                    continue;
                }
                // đọc từng ô
                for (Cell cell : row) {
                    int colIndex = cell.getColumnIndex();

                    CreateProductFromFileErrorResponse.CellDataError errorCol = new CreateProductFromFileErrorResponse.CellDataError();
                    errorCol.setColIndex(colIndex);
                    switch (colIndex) {
                        case 0:
                            ProductEntity existedProduct = productRepository.findByName(cell.getStringCellValue().trim()).orElse(null);
                            if (existedProduct != null) {
                                hasError = true;
                                rowValid = false;
                                errorCol.setErrorCode(1);
                                break;
                            }
                            product.setName(cell.getStringCellValue().trim());
                            break;
                        case 1:
                            CategoryEntity category = categoryRepository.findByName(cell.getStringCellValue().trim()).orElse(null);
                            if (category == null) {
                                hasError = true;
                                rowValid = false;
                                errorCol.setErrorCode(3);
                                break;
                            }
                            product.setCategory(category);
                            break;
                        case 2:
                            product.setStatus(ProductStatus.valueOf(cell.getStringCellValue()));
                            break;
                        case 3:
                            product.setDescription(cell.getStringCellValue().trim());
                            break;
                        case 4:
                            if ((long) cell.getNumericCellValue() < 1000) {
                                hasError = true;
                                rowValid = false;
                                errorCol.setErrorCode(6);
                                break;
                            }
                            product.setPrice((long) cell.getNumericCellValue());
                            break;
                        case 5:
                            String imageUrl = cell.getStringCellValue().trim();
                            AlbumEntity image = albumRepository.findByCloudinaryImageIdIsNotNullAndImageUrl(imageUrl.trim()).orElse(null);
                            if (image != null) {
                                if (image.getProduct() != null || image.getCategory() != null) {
                                    hasError = true;
                                    rowValid = false;
                                    errorCol.setErrorCode(4);
                                    break;
                                }
                                product.setImage(image);
                            } else {
                                AlbumEntity newImage = AlbumEntity.builder()
                                        .product(product)
                                        .type(AlbumType.PRODUCT)
                                        .thumbnailUrl(imageUrl)
                                        .imageUrl(imageUrl)
                                        .build();
                                product.setImage(newImage);
                            }
                            break;
                    }
                    if (errorCol.getErrorCode() != null) {
                        errorColList.add(errorCol);
                    }
                }

                System.out.println("Saving product " + product);
                if(rowValid) {
                    productValidList.add(product);
                }
                if(!errorRow.getErrorList().isEmpty()) {
                    data.add(errorRow);
                }

                inputStream.close();
                workbook.close();
            }
            if(force) {
                productRepository.saveAll(productValidList);
//                productSearchService.createAllProduct(productValidList);
            } else {
                if(!hasError) {
                    productRepository.saveAll(productValidList);
//                    productSearchService.createAllProduct(productValidList);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "The data is not in the correct format");
        } catch (ShopfeeException e) {
            throw new ShopfeeException(e.getError(), e.getDevMessage());
        } catch (Exception e) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.SERVER_ERROR, e.getMessage());
        }
        return data;
    }

    @Override
    public CheckExistedNameResponse isExistedProductName(String productName) {
        ProductEntity product = productRepository.findByName(productName).orElse(null);
        CheckExistedNameResponse data = new CheckExistedNameResponse();
        data.setExisted(product != null);
        return data;
    }

    @Override
    public byte[] downloadImportBeverageFile() {
        try {
            int rowEffected = 100;
            Workbook workbook = new XSSFWorkbook();
            Sheet dataSheet = workbook.createSheet("data");
            Sheet productSheet = workbook.createSheet("product");

            Row headerRow = dataSheet.createRow(0);
            String[] firstRow = {"Product name", "Category", "Status", "Description", "Image", "Size name", "Size price", "Topping name", "Topping price"};
            String[] firstRowData1 = {"Milk", "Milk tea", "AVAILABLE", "Delicious milk tea", "https://www.facebook.com/", "SMALL", "15000", "Flan", "2000"};
            String[] firstRowData2 = {null, null, null, null, null, "MEDIUM", "20000", null, null};
            String[] sizeNameArray = {ProductSize.SMALL.name(), ProductSize.MEDIUM.name(), ProductSize.LARGE.name()};
            String[] statusArray = {ProductStatus.AVAILABLE.name(), ProductStatus.HIDDEN.name(), ProductStatus.OUT_OF_STOCK.name()};
            for (int i = 0; i < firstRow.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(firstRow[i]);
            }
            // category value drop list
            List<String> categoryNameList = categoryRepository.getCategoryNameList();
            ExcelUtils.setDropList(categoryNameList.toArray(new String[0]), dataSheet, "Invalid Data", "Please select a value from the drop-down list.", 1, rowEffected, 1, 1);

            // size value drop list
            ExcelUtils.setDropList(sizeNameArray, dataSheet, "Invalid Data", "Please select a value from the drop-down list.", 1, rowEffected, 5, 5);

            // size status drop list
            ExcelUtils.setDropList(statusArray, dataSheet, "Invalid Data", "Please select a value from the drop-down list.", 1, rowEffected, 2, 2);

            // validate price > 1000
            ExcelUtils.setIntegerConstraint(dataSheet, 999, 9999999, "Invalid Data", "Price must be greater than 999.", 1, rowEffected, 6, 6);
            ExcelUtils.setIntegerConstraint(dataSheet, 999, 9999999, "Invalid Data", "Price must be greater than 999.", 1, rowEffected, 8, 8);

            // validate product name
            List<String> productNameList = productRepository.getProductNameList();
            for (int i = 0; i < productNameList.size(); i++) {
                Row row = productSheet.createRow(i);
                row.createCell(0).setCellValue(productNameList.get(i));
            }

            String rangeName = "productName";
            String reference = "product!$A$1:$A$" + (productNameList.size());
            ExcelUtils.setFormulas(workbook, rangeName, reference);
            ExcelUtils.setCustomConstraint(dataSheet, "COUNTIF(productName, A2)=0", "Invalid Data", "The product name is already in the database", 1, rowEffected, 0, 0);

            // tao data mau
            Row r1 = dataSheet.createRow(1);
            Row r2 = dataSheet.createRow(2);
            for (int i = 0; i < firstRowData1.length; i++) {
                Cell cell1 = r1.createCell(i);
                Cell cell2 = r2.createCell(i);
                if (i == 6 || i == 8) {
                    if (firstRowData1[i] != null) {
                        cell1.setCellValue(Long.parseLong(firstRowData1[i]));
                    }
                    if (firstRowData2[i] != null) {
                        cell2.setCellValue(Long.parseLong(firstRowData2[i]));

                    }
                    continue;
                }
                cell1.setCellValue(firstRowData1[i]);
                cell2.setCellValue(firstRowData2[i]);
            }

            for (int i = 0; i <= 4; i++) {
                dataSheet.addMergedRegion(new CellRangeAddress(1, 2, i, i));
            }
            for (Row row : dataSheet) {
                row.setHeight((short) -1);
                for (Cell cell : row) {
                    dataSheet.autoSizeColumn(cell.getColumnIndex());
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream("beverage.xlsx")) {
                workbook.write(fileOut);
            }
            workbook.close();
            File file = new File("./beverage.xlsx");
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] downloadImportCakeFile() {
        try {
            int rowEffected = 100;
            Workbook workbook = new XSSFWorkbook();
            Sheet dataSheet = workbook.createSheet("data");
            Sheet productSheet = workbook.createSheet("product");

            Row headerRow = dataSheet.createRow(0);
            String[] firstRow = {"Product name", "Category", "Status", "Description", "Price", "Image"};
            String[] firstRowData1 = {"Cinnamon cone", "Sweet cake", "AVAILABLE", "Cinnamon and sweet cake", "2000", "https://www.facebook.com/"};

            String[] statusArray = {ProductStatus.AVAILABLE.name(), ProductStatus.HIDDEN.name(), ProductStatus.OUT_OF_STOCK.name()};
            for (int i = 0; i < firstRow.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(firstRow[i]);
            }
            // category value drop list
            List<String> categoryNameList = categoryRepository.getCategoryNameList();
            ExcelUtils.setDropList(categoryNameList.toArray(new String[0]), dataSheet, "Invalid Data", "Please select a value from the drop-down list.", 1, rowEffected, 1, 1);


            // size status drop list
            ExcelUtils.setDropList(statusArray, dataSheet, "Invalid Data", "Please select a value from the drop-down list.", 1, rowEffected, 2, 2);

            // validate price > 1000
            ExcelUtils.setIntegerConstraint(dataSheet, 999, 9999999, "Invalid Data", "Price must be greater than 999.", 1, rowEffected, 4, 4);

            // validate product name
            List<String> productNameList = productRepository.getProductNameList();
            for (int i = 0; i < productNameList.size(); i++) {
                Row row = productSheet.createRow(i);
                row.createCell(0).setCellValue(productNameList.get(i));
            }

            String rangeName = "productName";
            String reference = "product!$A$1:$A$" + (productNameList.size());
            ExcelUtils.setFormulas(workbook, rangeName, reference);
            ExcelUtils.setCustomConstraint(dataSheet, "COUNTIF(productName, A2)=0", "Invalid Data", "The product name is already in the database", 1, rowEffected, 0, 0);

            // tao data mau
            Row r1 = dataSheet.createRow(1);
            for (int i = 0; i < firstRowData1.length; i++) {
                Cell cell1 = r1.createCell(i);
                if (i == 4) {
                    cell1.setCellValue(Long.valueOf(firstRowData1[i]));
                    continue;
                }
                cell1.setCellValue(firstRowData1[i]);
            }

            for (Row row : dataSheet) {
                row.setHeight((short) -1);
                for (Cell cell : row) {
                    dataSheet.autoSizeColumn(cell.getColumnIndex());
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream("cake.xlsx")) {
                workbook.write(fileOut);
            }
            workbook.close();
            File file = new File("./cake.xlsx");
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
