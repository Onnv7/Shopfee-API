package com.hcmute.shopfee.service.elasticsearch;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.database.product.ProductEntity;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import com.hcmute.shopfee.repository.elasticsearch.ProductSearchRepository;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.core.impl.ProductService;
import com.hcmute.shopfee.utils.RegexUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductSearchRepository productSearchRepository;

    public ProductIndex createProduct(ProductEntity data) {

        ProductIndex dataSearch = ProductIndex.builder()
                .id(data.getId())
                .name(data.getName())
                .thumbnailUrl(data.getThumbnailUrl())
                .description(data.getDescription())
                .status(data.getStatus())
                .categoryId(data.getCategory().getId())
                .type(data.getType())
                .isDeleted(data.isDeleted())
                .price(data.getPrice())
                .build();

        return productSearchRepository.save(dataSearch);
    }

    public void upsertProduct(ProductEntity data) {
        ProductIndex product = productSearchRepository.findById(data.getId()).orElse(null);
        if (product != null) {
            product.setName(data.getName());
            product.setThumbnailUrl(data.getThumbnailUrl());
            product.setStatus(data.getStatus());
            product.setPrice(data.getPrice());
            product.setDescription(data.getDescription());
            product.setDeleted(data.isDeleted());
            product.setCategoryId(data.getCategory().getId());
            productSearchRepository.save(product);
        } else {
            createProduct(data);
        }
    }

    public void deleteProduct(String id) {
        ProductIndex productIndex = productSearchRepository.findById(id).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));
        productIndex.setDeleted(true);
        productSearchRepository.save(productIndex);
    }

    public Page<ProductIndex> searchVisibleProduct(String key, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        String textRegex = RegexUtils.generateFilterRegexString(key);
        return productSearchRepository.searchVisibleProduct(key, textRegex, pageable);
    }

    public Page<ProductIndex> searchProduct(String key, String categoryIdRegex, String productStatusRegex, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productSearchRepository.searchProduct(key, categoryIdRegex, productStatusRegex, pageable);
    }
}
