package com.hcmute.shopfee.service.redis;

import com.hcmute.shopfee.model.redis.UserToken;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRefreshTokenRedisService {
    private final String USER_REFRESH_TOKEN_KEY = "UserRefreshToken";
    private final ModelMapper modelMapper;
    private final RedisTemplate redisTemplate;


    public void createNewUserRefreshToken(String refreshToken, String userId) {
        UserToken data = UserToken.builder()
                .refreshToken(refreshToken)
                .isUsed(false)
                .userId(userId)
                .build();
        redisTemplate.opsForList().rightPush(USER_REFRESH_TOKEN_KEY + ":" + userId, data);
    }

    public List<UserToken> getUserRefreshTokenList(String userId) {
        return modelMapper.map(redisTemplate.opsForList().range(
                USER_REFRESH_TOKEN_KEY + ":" + userId, 0, -1),
                new TypeToken<List<UserToken>>() {}.getType()
        );
    }

    public UserToken getInfoOfRefreshToken(String refreshToken, String userId) {
        List<UserToken> refreshTokenList = getUserRefreshTokenList(userId);
        UserToken targetToken = refreshTokenList.stream()
                .filter(it -> it.getRefreshToken().equals(refreshToken))
                .findFirst().orElse(null);
        return targetToken;
    }

    public void updateUsedUserRefreshToken(UserToken oldValue) {
        int index = Math.toIntExact(redisTemplate.opsForList().indexOf(USER_REFRESH_TOKEN_KEY + ":" + oldValue.getUserId(), oldValue));
        oldValue.setUsed(true);
        redisTemplate.opsForList().set(USER_REFRESH_TOKEN_KEY + ":"+oldValue.getUserId(), index, oldValue);
    }
    public void deleteUserRefreshToken(String userId) {
        redisTemplate.delete(USER_REFRESH_TOKEN_KEY + ":" + userId);
    }
}
