package com.hcmute.shopfee.entity.sql.listener;

import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.service.elasticsearch.ProductESService;
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
    private ProductESService productESService;

    @PostPersist
    public void postPersist(ProductEntity entity) {
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VIEW, entity.getId()));
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VISIBLE, "*"));
        productESService.createProduct(entity);
    }

    @PostUpdate
    public void postUpdate(ProductEntity entity) {
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VIEW, entity.getId()));
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VISIBLE, "*"));
        productESService.upsertProduct(entity);
    }

    @PostRemove
    public void postRemove(ProductEntity entity) {
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VIEW, entity.getId()));
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VISIBLE, "*"));

        productESService.deleteProduct(entity.getId());
    }
}
