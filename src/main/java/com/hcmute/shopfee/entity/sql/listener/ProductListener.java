package com.hcmute.shopfee.entity.sql.listener;

import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.service.elasticsearch.ProductSearchService;
import com.hcmute.shopfee.service.redis.ProductRedisService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@RequiredArgsConstructor
public class ProductListener {
    private final ProductRedisService productRedisService;
    @Autowired
    @Lazy
    private  ProductSearchService productSearchService;

    @PostPersist
    public void postPersist(ProductEntity entity) {
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VIEW, entity.getId()));
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VISIBLE, "*"));
        productSearchService.createProduct(entity);
    }

    @PostUpdate
    public void postUpdate(ProductEntity entity) {
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VIEW, entity.getId()));
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VISIBLE, "*"));
        productSearchService.upsertProduct(entity);
    }

    @PostRemove
    public void postRemove(ProductEntity entity) {
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VIEW, entity.getId()));
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VISIBLE, "*"));

        productSearchService.deleteProduct(entity.getId());
    }
}
