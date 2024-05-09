package com.hcmute.shopfee.service.elasticsearch;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.repository.elasticsearch.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductSearchRepository productSearchRepository;
    @Autowired
    @Lazy
    private ProductRepository productRepository;

    public void syncProductIndexAndDatabase() {
        productSearchRepository.deleteAll();
        List<ProductEntity> productEntityList = productRepository.findAll();
        for (ProductEntity productEntity : productEntityList) {
            createProduct(productEntity);
        }
    }
    public ProductIndex createProduct(ProductEntity data) {

        ProductIndex dataSearch = ProductIndex.builder()
                .id(data.getId())
                .name(data.getName())
                .thumbnailUrl(data.getImage().getThumbnailUrl())
                .description(data.getDescription())
                .status(data.getStatus())
                .categoryId(data.getCategory().getId())
                .type(data.getType())
                .price(data.getPrice())
                .build();

        return productSearchRepository.save(dataSearch);
    }

    public void upsertProduct(ProductEntity data) {
        ProductIndex product = productSearchRepository.findById(data.getId()).orElse(null);
        if (product != null) {
            product.setName(data.getName());
            product.setThumbnailUrl(data.getImage().getThumbnailUrl());
            product.setStatus(data.getStatus());
            product.setPrice(data.getPrice());
            product.setDescription(data.getDescription());
            product.setCategoryId(data.getCategory().getId());
            productSearchRepository.save(product);
        } else {
            createProduct(data);
        }
    }

    public void deleteProduct(String id) {
        ProductIndex productIndex = productSearchRepository.findById(id)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + id));
        productSearchRepository.delete(productIndex);
    }

    public Page<ProductIndex> searchVisibleProduct(String key,  Pageable pageable) {
//        Pageable pageable = PageRequest.of(page - 1, size);
//        String textRegex = RegexUtils.generateFilterRegexString(key);
        return productSearchRepository.searchVisibleProduct(key, pageable);
    }

    public Page<ProductIndex> searchProduct(String key, String categoryIdRegex, String productStatusRegex, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productSearchRepository.searchProduct(key, categoryIdRegex, productStatusRegex, pageable);
    }
}
