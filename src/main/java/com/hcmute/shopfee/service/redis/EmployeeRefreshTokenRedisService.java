package com.hcmute.shopfee.service.redis;

import com.hcmute.shopfee.model.redis.EmployeeToken;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EmployeeRefreshTokenRedisService {
    private final String EMPLOYEE_REFRESH_TOKEN_KEY = "EmployeeRefreshToken";
    private final ModelMapper modelMapper;
    private final RedisTemplate redisTemplate;

    public void createNewEmployeeRefreshToken(String refreshToken, String employeeId) {
        EmployeeToken data = EmployeeToken.builder()
                .refreshToken(refreshToken)
                .isUsed(false)
                .employeeId(employeeId)
                .build();
        redisTemplate.opsForList().rightPush(EMPLOYEE_REFRESH_TOKEN_KEY + ":" + employeeId, data);
    }

    public List<EmployeeToken> getEmployeeRefreshTokenList(String employeeId) {
        return modelMapper.map(redisTemplate.opsForList().range(
                        EMPLOYEE_REFRESH_TOKEN_KEY + ":" + employeeId, 0, -1),
                new TypeToken<List<EmployeeToken>>() {}.getType()
        );
    }

    public EmployeeToken getInfoOfRefreshToken(String refreshToken, String userId) {
        List<EmployeeToken> refreshTokenList = getEmployeeRefreshTokenList(userId);
        EmployeeToken targetToken = refreshTokenList.stream()
                .filter(it -> it.getRefreshToken().equals(refreshToken))
                .findFirst().orElse(null);
        return targetToken;
    }

    public void updateUsedEmployeeRefreshToken(EmployeeToken oldValue) {
        int index = Math.toIntExact(redisTemplate.opsForList().indexOf(EMPLOYEE_REFRESH_TOKEN_KEY + ":" + oldValue.getEmployeeId(), oldValue));
        oldValue.setUsed(true);
        redisTemplate.opsForList().set(EMPLOYEE_REFRESH_TOKEN_KEY + ":"+oldValue.getEmployeeId(), index, oldValue);
    }
    public void deleteUserRefreshToken(String employeeId) {
        redisTemplate.delete(EMPLOYEE_REFRESH_TOKEN_KEY + ":" + employeeId);
    }
}
