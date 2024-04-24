package com.hcmute.shopfee.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.constant.ShopfeeConstant;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.entity.redis.EmployeeTokenEntity;
import com.hcmute.shopfee.repository.redis.EmployeeTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class EmployeeTokenRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    public static final String PREFIX_KEY_EMPLOYEE_TOKEN = "employee_token";
    public final static String STRING_FORMAT_KEY_EMPLOYEE_TOKEN = PREFIX_KEY_EMPLOYEE_TOKEN + ":%s:%s";

    private String getKeyEmployeeToken(String employeeId, String token) {
        return String.format(STRING_FORMAT_KEY_EMPLOYEE_TOKEN, employeeId, token);
    }
    public void saveEmployeeToken(String employeeId, String token, boolean value) {
        String key = getKeyEmployeeToken(employeeId, token);
        redisTemplate.opsForValue().set(key, value,  ShopfeeConstant.REFRESH_TOKEN_EXPIRE_MINUTES_TIME, TimeUnit.MINUTES);
    }
    public Boolean getEmployeeTokenValue(String employeeId, String token) {
        String key = getKeyEmployeeToken(employeeId, token);
        return (Boolean) redisTemplate.opsForValue().get(key);
    }
    public void deleteAllTokenOfEmployee(String employeeId) {
        String pattern = getKeyEmployeeToken(employeeId, "*");
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }
}
