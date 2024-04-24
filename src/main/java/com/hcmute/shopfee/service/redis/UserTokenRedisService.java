package com.hcmute.shopfee.service.redis;

import com.hcmute.shopfee.constant.ShopfeeConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserTokenRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    public static final String PREFIX_KEY_USER_TOKEN = "user_token";
    public final static String STRING_FORMAT_KEY_USER_TOKEN = PREFIX_KEY_USER_TOKEN + ":%s:%s";

    private String getKeyUserTokenKey(String userId, String token) {
        return String.format(STRING_FORMAT_KEY_USER_TOKEN, userId, token);
    }
    public void upsertUserToken(String userId, String token, boolean isUsed) {
        String key = getKeyUserTokenKey(userId, token);
        redisTemplate.opsForValue().set(key, isUsed, ShopfeeConstant.REFRESH_TOKEN_EXPIRE_MINUTES_TIME, TimeUnit.MINUTES);
    }
    public Boolean getUserTokenValue(String userId, String token) {
        String key = getKeyUserTokenKey(userId, token);
        return (Boolean) redisTemplate.opsForValue().get(key);
    }
    public void deleteAllTokenOfUser(String userId) {
        String pattern = getKeyUserTokenKey(userId, "*");
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }
}
