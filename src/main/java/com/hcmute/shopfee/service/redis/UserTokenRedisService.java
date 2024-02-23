package com.hcmute.shopfee.service.redis;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.redis.EmployeeTokenEntity;
import com.hcmute.shopfee.entity.redis.UserTokenEntity;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.redis.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTokenRedisService {
    private final UserTokenRepository userTokenRepository;

    public void createNewUserRefreshToken(String refreshToken, String userId) {
        UserTokenEntity data = UserTokenEntity.builder()
                .refreshToken(refreshToken)
                .isUsed(false)
                .userId(userId)
                .build();
        userTokenRepository.save(data);
    }
    public void deleteByUserIdAndRefreshToken(String userId, String refreshToken) {
        UserTokenEntity entity = userTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
        userTokenRepository.delete(entity);
    }

    public UserTokenEntity getInfoOfRefreshToken(String refreshToken, String userId) {
        UserTokenEntity entity = userTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
        return entity;
    }

    public void updateUsedUserRefreshToken(UserTokenEntity oldValue) {
        oldValue.setUsed(true);
        userTokenRepository.save(oldValue);
    }
    public void deleteAllTokenByUserId(String userId) {
        List<UserTokenEntity> userTokenEntityList = userTokenRepository.findByUserId(userId);
        for(UserTokenEntity userTokenEntity : userTokenEntityList) {
            userTokenRepository.delete(userTokenEntity);
        }
    }
}
