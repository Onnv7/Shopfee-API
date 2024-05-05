package com.hcmute.shopfee.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.shopfee.dto.response.GetAllVisibleProductResponse;
import com.hcmute.shopfee.dto.response.GetProductViewByIdResponse;
import com.hcmute.shopfee.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper = new ObjectMapper();
    public final static String STRING_FORMAT_KEY_GET_PRODUCT_VIEW = "get_product_view:%s";
    public final static String PATTERN_KEY_GET_PRODUCT_VIEW = "get_product_view:%s";
    public final static String STRING_FORMAT_KEY_GET_PRODUCT_VISIBLE = "get_product_visible:%d:%d:%s:%s:%d:%d:%d";
    public final static String PATTERN_KEY_GET_PRODUCT_VISIBLE = "get_product_visible:*";

    private String getKeyForGetProductVisibleList(PageRequest pageRequest, String keyword, Long minPrice, Long maxPrice, Integer minStar) {
//        String sortDirection = pageRequest.getSort().getOrderFor("id").getProperty()
        Sort.Order sortOrder = pageRequest.getSort().getOrderFor("price");
        String sortDirection = "";
        if (sortOrder != null) {
            sortDirection = sortOrder.getDirection() == Sort.Direction.ASC ? "ASC" : "DESC";
        }
        return String.format(STRING_FORMAT_KEY_GET_PRODUCT_VISIBLE, pageRequest.getPageNumber(), pageRequest.getPageSize(), keyword, sortDirection, minPrice, maxPrice, minStar);
    }

    public GetAllVisibleProductResponse getProductVisibleList(String keyword, PageRequest pageRequest, Long minPrice, Long maxPrice, Integer minStar) throws JsonProcessingException {
        String key = getKeyForGetProductVisibleList(pageRequest, keyword, minPrice, maxPrice, minStar);
        String json = (String) redisTemplate.opsForValue().get(key);
        return json != null ? redisObjectMapper.readValue(json, new TypeReference<GetAllVisibleProductResponse>() {
        }) : null;
    }

    public void saveProductVisibleList(GetAllVisibleProductResponse productEntityList, String keyword, PageRequest pageRequest, Long minPrice, Long maxPrice, Integer minStar) throws JsonProcessingException {
        String key = getKeyForGetProductVisibleList(pageRequest, keyword, minPrice, maxPrice, minStar);
        String json = redisObjectMapper.writeValueAsString(productEntityList);
        redisTemplate.opsForValue().set(key, json);

    }

    public void deleteKeysWithPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    private String getKeyForGetProductView(String productId) {
        return String.format(STRING_FORMAT_KEY_GET_PRODUCT_VIEW, productId);
    }

    public void saveProductView(GetProductViewByIdResponse product) throws JsonProcessingException {
        String key = getKeyForGetProductView(product.getId());
        String json = redisObjectMapper.writeValueAsString(product);
        redisTemplate.opsForValue().set(key, json);
    }

    public GetProductViewByIdResponse getProductView(String productId) throws JsonProcessingException {
        String key = getKeyForGetProductView(productId);
        String json = (String) redisTemplate.opsForValue().get(key);
        return json != null ? redisObjectMapper.readValue(json, new TypeReference<GetProductViewByIdResponse>() {
        }) : null;
    }
}
