package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.request.CreateProductRequest;
import com.hcmute.shopfee.dto.request.UpdateProductRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.database.CategoryEntity;
import com.hcmute.shopfee.entity.database.product.ProductEntity;
import com.hcmute.shopfee.entity.database.product.SizeEntity;
import com.hcmute.shopfee.entity.database.product.ToppingEntity;
import com.hcmute.shopfee.enums.ProductSize;
import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
import com.hcmute.shopfee.enums.SortType;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import com.hcmute.shopfee.repository.database.CategoryRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.repository.database.review.ProductReviewRepository;
import com.hcmute.shopfee.service.core.IProductService;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.elasticsearch.ProductSearchService;
import com.hcmute.shopfee.utils.ImageUtils;
import com.hcmute.shopfee.utils.RegexUtils;
import com.hcmute.shopfee.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ModelMapperService modelMapperService;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductSearchService productSearchService;
    private final ProductReviewRepository productReviewRepository;


    public static long getMinPrice(List<SizeEntity> sizeList) {
        long min = sizeList.get(0).getPrice();
        for (SizeEntity item : sizeList) {
            if (min > item.getPrice()) {
                min = item.getPrice();
            }
        }
        return min;
    }

    @Override
    public void createProduct(CreateProductRequest body, MultipartFile image, ProductType productType) {
        if (!ImageUtils.isValidImageFile(body.getImage())) {
            throw new CustomException(ErrorConstant.IMAGE_INVALID);
        }
        if ((productType == ProductType.BEVERAGE && (body.getSizeList() == null || body.getPrice() != null))) {
            throw new CustomException(ErrorConstant.PARAMETER_INVALID);
        } else if (productType == ProductType.CAKE) {
            if (body.getToppingList() != null || body.getSizeList() != null || body.getPrice() == null) {
                throw new CustomException(ErrorConstant.PARAMETER_INVALID);
            }
        }
        if (productRepository.findByNameAndIsDeletedFalse(body.getName()).orElse(null) != null) {
            throw new CustomException(ErrorConstant.PRODUCT_NAME_EXISTED);
        }

        ProductEntity productEntity = modelMapperService.mapClass(body, ProductEntity.class);
        if (body.getToppingList() != null) {
            List<ToppingEntity> toppingList = ToppingEntity.fromToppingDtoList(body.getToppingList(), productEntity);
            productEntity.setToppingList(toppingList);
        }

        if (body.getSizeList() != null) {
            List<SizeEntity> sizeList = SizeEntity.fromToppingDtoList(body.getSizeList(), productEntity);
            productEntity.setSizeList(sizeList);
        }
        productEntity.setType(productType);
        productEntity.setName(body.getName());
        productEntity.setDescription(body.getDescription());
        productEntity.setStatus(body.getStatus());
        productEntity.setDeleted(false);


        if (productType == ProductType.CAKE) {
            productEntity.setPrice(body.getPrice());
        } else if (productType == ProductType.BEVERAGE) {
            productEntity.setPrice(getMinPrice(productEntity.getSizeList()));
        }
        byte[] originalImage = new byte[0];
        try {
            originalImage = image.getBytes();
            byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);

            CategoryEntity categoryEntity = categoryRepository.findByIdAndIsDeletedFalse(body.getCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getCategoryId()));

            productEntity.setCategory(categoryEntity);
            HashMap<String, String> imageUploaded = cloudinaryService.uploadFileToFolder(
                    CloudinaryConstant.PRODUCT_PATH,
                    StringUtils.generateFileName(body.getName(), "product"),
                    newImage
            );

            productEntity.setImageId(imageUploaded.get(CloudinaryConstant.PUBLIC_ID));
            productEntity.setImageUrl(imageUploaded.get(CloudinaryConstant.URL_PROPERTY));
            productEntity.setThumbnailUrl(cloudinaryService.getThumbnailUrl(imageUploaded.get(CloudinaryConstant.PUBLIC_ID)));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        data.setCode(sequenceService.generateCode(ProductCollection.SEQUENCE_NAME, ProductCollection.PREFIX_CODE, ProductCollection.LENGTH_NUMBER));

        ProductEntity dataSaved = productRepository.save(productEntity);
        productSearchService.createProduct(dataSaved);
    }

    @Override
    public GetProductByIdResponse getProductDetailsById(String id) {
        ProductEntity product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));
        GetProductByIdResponse result = modelMapperService.mapClass(product, GetProductByIdResponse.class);
        result.setCategoryId(product.getCategory().getId());
        return result;
    }

    @Override
    public GetProductViewByIdResponse getProductViewById(String id) {
        ProductEntity product = productRepository.findByIdAndStatusNotAndIsDeletedFalse(id, ProductStatus.HIDDEN)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));
        GetProductViewByIdResponse data = modelMapperService.mapClass(product, GetProductViewByIdResponse.class);
        RatingSummaryQueryDto ratingSummaryQueryDto = productReviewRepository.getRatingSummary(product.getId());
        data.setRatingSummary(RatingSummaryDto.fromRatingSummaryDto(ratingSummaryQueryDto));
        return data;
    }

    @Override
    public GetProductsByCategoryIdResponse getProductsByCategoryId(String categoryId, Long minPrice, Long maxPrice, int minStar, SortType sortType, int page, int size) {
        GetProductsByCategoryIdResponse data = new GetProductsByCategoryIdResponse();
        List<GetProductsByCategoryIdResponse.ProductCard> productList = new ArrayList<>();

        data.setProductList(productList);
        // TODO: nên check category not hidden
        Page<ProductEntity> productPage = null;

        Pageable pageable = PageRequest.of(page - 1, size);
        if (minPrice != null && maxPrice != null) {
            if (sortType == SortType.PRICE_DESC) {
                pageable = PageRequest.of(page - 1, size, Sort.by("price").descending());
            } else if (sortType == SortType.PRICE_ASC) {
                pageable = PageRequest.of(page - 1, size, Sort.by("price").ascending());
            }
            productPage = productRepository.getProductByCategoryIdAndFilter(categoryId, minPrice, maxPrice, minStar, pageable);

        } else {
            productPage = productRepository.findByCategory_IdAndStatusNotAndIsDeletedFalse(categoryId, ProductStatus.HIDDEN, PageRequest.of(page - 1, size));
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
    public GetAllVisibleProductResponse getVisibleProductList(Long minPrice, Long maxPrice, int minStar, SortType sortType, int page, int size, String key) {
        GetAllVisibleProductResponse data = new GetAllVisibleProductResponse();

        List<GetAllVisibleProductResponse.ProductCard> productList = new ArrayList<>();
        data.setProductList(productList);

        Pageable pageable = PageRequest.of(page - 1, size);

        if (key != null) {
            Page<ProductIndex> productIndexPage = productSearchService.searchVisibleProduct(key, page, size);
            data.setTotalPage(productIndexPage.getTotalPages());
            List<ProductIndex> productIndexList = productIndexPage.getContent();
            for (ProductIndex index : productIndexList) {
                RatingSummaryQueryDto ratingSummaryQueryDto = productReviewRepository.getRatingSummary(index.getId());
                productList.add(GetAllVisibleProductResponse.ProductCard.fromProductIndex(index, ratingSummaryQueryDto));
            }
        } else {
            Page<ProductEntity> productPage = null;
            if (minPrice != null && maxPrice != null) {
                if (sortType == SortType.PRICE_DESC) {
                    pageable = PageRequest.of(page - 1, size, Sort.by("price").descending());
                } else if (sortType == SortType.PRICE_ASC) {
                    pageable = PageRequest.of(page - 1, size, Sort.by("price").ascending());
                }

                productPage = productRepository.getAllProductAndFilter(minPrice, maxPrice, minStar, pageable);
            } else {
                 productPage = productRepository.findByStatusNotAndIsDeletedFalse(ProductStatus.HIDDEN, pageable);

            }
            data.setTotalPage(productPage.getTotalPages());
            List<ProductEntity> productEntityList = productPage.getContent();
            for (ProductEntity entity : productEntityList) {
                RatingSummaryQueryDto ratingSummaryQueryDto = productReviewRepository.getRatingSummary(entity.getId());
                productList.add(GetAllVisibleProductResponse.ProductCard.fromProductEntity(entity, ratingSummaryQueryDto));
            }
        }
        return data;
    }

    @Override
    public GetProductListResponse getProductList(String key, int page, int size, String categoryId, ProductStatus productStatus) {
        Pageable pageable = PageRequest.of(page - 1, size);
        String categoryIdRegex = RegexUtils.generateFilterRegexString(categoryId != null ? categoryId : "");
        String productStatusRegex = RegexUtils.generateFilterRegexString(productStatus != null ? productStatus.toString() : "");
        if (key == null) {
            Page<ProductEntity> productPage = productRepository.getProductList(categoryIdRegex, productStatusRegex, pageable);
            GetProductListResponse productList = new GetProductListResponse();
            productList.setTotalPage(productPage.getTotalPages());
            productList.setProductList(modelMapperService.mapList(productPage.getContent(), GetProductListResponse.Product.class));
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
        ProductEntity product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));
        if (productRepository.countOrderItem(id) == 0) {
            product.setDeleted(true);
            productRepository.save(product);
            productSearchService.deleteProduct(id);
        } else {
            throw new CustomException(ErrorConstant.CANT_DELETE);
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
    public void updateProductById(UpdateProductRequest body, String id) {
        ProductEntity product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));

        modelMapperService.map(body, product);

        if (body.getImage() != null) {
            try {
                cloudinaryService.deleteImage(product.getImageId());

                byte[] originalImage = body.getImage().getBytes();

                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                HashMap<String, String> fileUploaded = cloudinaryService.uploadFileToFolder(CloudinaryConstant.PRODUCT_PATH,
                        StringUtils.generateFileName(body.getName(), "product"), newImage);
                product.setImageId(fileUploaded.get(CloudinaryConstant.PUBLIC_ID));
                product.setImageUrl(fileUploaded.get(CloudinaryConstant.URL_PROPERTY));
                product.setThumbnailUrl(cloudinaryService.getThumbnailUrl(fileUploaded.get(CloudinaryConstant.PUBLIC_ID)));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        product.setPrice(getMinPrice(product.getSizeList()));

        CategoryEntity category = categoryRepository.findByIdAndIsDeletedFalse(body.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));
        product.setCategory(category);

        productRepository.save(product);
        productSearchService.upsertProduct(product);
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
        for (ProductEntity entity : productEntityList) {
            RatingSummaryQueryDto ratingSummaryQueryDto = productReviewRepository.getRatingSummary(entity.getId());
            data.add(GetTopSellingProductResponse.fromProductEntity(entity, ratingSummaryQueryDto));
        }

        return data;
    }

    @Override
    public void createBeverageFromFile(MultipartFile file) {
        int success = 0;
        List<ProductEntity> productList = new ArrayList<>();
        InputStream inputStream = null;
        Workbook workbook = null;
        try {
            inputStream = file.getInputStream();
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Sheet sheet = workbook.getSheetAt(0);

        ProductEntity product = null;
        List<SizeEntity> sizeEntityList = new ArrayList<>();
        List<ToppingEntity> toppingEntityList = new ArrayList<>();
        boolean newProductFlag = true;

        // đọc từng hàng
        for (Row row : sheet) {
            int rowIndex = row.getRowNum();
            if (rowIndex == 0) {
                continue;
            }

            SizeEntity sizeEntity = new SizeEntity();
            ToppingEntity toppingEntity = new ToppingEntity();
            if (row.getCell(0).getCellType() == CellType.BLANK) {
                newProductFlag = false;
            } else {
                newProductFlag = true;
            }
            if (newProductFlag) {
                product = new ProductEntity();
                sizeEntityList = new ArrayList<>();
                product.setSizeList(sizeEntityList);

                toppingEntityList = new ArrayList<>();
                product.setToppingList(toppingEntityList);
            }

            // đọc từng ô
            for (Cell cell : row) {
                int colIndex = cell.getColumnIndex();
                if (cell.getCellType() == CellType.BLANK || (cell.getCellType() == CellType.STRING && cell.getStringCellValue().isEmpty())) {
                    continue;
                }
                switch (colIndex) {
                    case 0:
                        product.setName(cell.getStringCellValue());
                        break;
                    case 1:
                        CategoryEntity category = categoryRepository.findByIdAndIsDeletedFalse(cell.getStringCellValue())
                                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + cell.getStringCellValue()));
                        product.setCategory(category);
                        break;
                    case 2:
                        product.setStatus(ProductStatus.valueOf(cell.getStringCellValue()));
                        break;
                    case 3:
                        product.setDescription(cell.getStringCellValue());
                        break;
                    case 4:
                        sizeEntity.setSize(ProductSize.valueOf(cell.getStringCellValue()));
                        break;
                    case 5:
                        sizeEntity.setPrice((long) cell.getNumericCellValue());

                        SizeEntity newEntity = new SizeEntity();
                        newEntity.setSize(sizeEntity.getSize());
                        newEntity.setPrice(sizeEntity.getPrice());
                        newEntity.setProduct(product);
                        sizeEntityList.add(newEntity);
                        break;
                    case 6:
                        toppingEntity.setName(cell.getStringCellValue());

                        break;
                    case 7:
                        toppingEntity.setPrice((long) cell.getNumericCellValue());

                        ToppingEntity newToppingEntity = new ToppingEntity();
                        newToppingEntity.setProduct(product);
                        newToppingEntity.setName(toppingEntity.getName());
                        newToppingEntity.setPrice(toppingEntity.getPrice());

                        toppingEntityList.add(newToppingEntity);
                        break;

                }
            }
            System.out.println(product);
            System.out.println("================================================");
        }
        try {
            inputStream.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createCakeFromFile(MultipartFile file) {
        int success = 0;
        List<ProductEntity> productList = new ArrayList<>();
        InputStream inputStream = null;
        Workbook workbook = null;
        try {
            inputStream = file.getInputStream();
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Sheet sheet = workbook.getSheetAt(0);

        // đọc từng hàng
        for (Row row : sheet) {
            int rowIndex = row.getRowNum();
            if (rowIndex == 0) {
                continue;
            }
            ProductEntity product = new ProductEntity();

            // đọc từng ô
            for (Cell cell : row) {
                int colIndex = cell.getColumnIndex();
                if (cell.getCellType() == CellType.BLANK || (cell.getCellType() == CellType.STRING && cell.getStringCellValue().isEmpty())) {
                    continue;
                }
                switch (colIndex) {
                    case 0:
                        product.setName(cell.getStringCellValue());
                        break;
                    case 1:
                        CategoryEntity category = categoryRepository.findByIdAndIsDeletedFalse(cell.getStringCellValue())
                                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + cell.getStringCellValue()));
                        product.setCategory(category);
                        break;
                    case 2:
                        product.setStatus(ProductStatus.valueOf(cell.getStringCellValue()));
                        break;
                    case 3:
                        product.setDescription(cell.getStringCellValue());
                        break;
                    case 4:
                        product.setPrice((long) cell.getNumericCellValue());
                        break;
                }
            }
            System.out.println(product);
        }
        try {
            inputStream.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
