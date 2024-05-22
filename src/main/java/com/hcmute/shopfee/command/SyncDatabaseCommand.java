package com.hcmute.shopfee.command;

import com.hcmute.shopfee.service.elasticsearch.OrderESService;
import com.hcmute.shopfee.service.elasticsearch.ProductESService;
import com.hcmute.shopfee.service.elasticsearch.RatingProductESService;
import com.hcmute.shopfee.service.redis.ProductRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class SyncDatabaseCommand implements CommandLineRunner {
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Lazy
    private ProductESService productESService;
    private final OrderESService orderESService;
    private final RatingProductESService ratingProductESService;
    @Override
    public void run(String... args) throws Exception {
        productESService.syncProductIndexAndDatabase();
        orderESService.syncOrderIndexAndDatabase();
        ratingProductESService.syncProductIndexAndDatabase();
        clearCacheByPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VIEW, "*"));
        clearCacheByPattern(String.format(ProductRedisService.PATTERN_KEY_GET_PRODUCT_VISIBLE, "*"));
    }

    public void clearCacheByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }

}
