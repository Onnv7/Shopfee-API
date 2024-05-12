package com.hcmute.shopfee.entity.sql.listener;

import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.service.elasticsearch.ProductEService;
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
    private ProductEService productEService;

    @PostPersist
    public void postPersist(ProductEntity entity) {
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VIEW, entity.getId()));
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VISIBLE, "*"));
        productEService.createProduct(entity);
    }

    @PostUpdate
    public void postUpdate(ProductEntity entity) {
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VIEW, entity.getId()));
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VISIBLE, "*"));
        productEService.upsertProduct(entity);
    }

    @PostRemove
    public void postRemove(ProductEntity entity) {
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VIEW, entity.getId()));
        productRedisService.deleteKeysWithPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VISIBLE, "*"));

        productEService.deleteProduct(entity.getId());
    }
}
