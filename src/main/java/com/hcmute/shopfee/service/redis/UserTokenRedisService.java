package com.hcmute.shopfee.service.redis;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.redis.UserTokenEntity;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.redis.UserTokenRepository;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.USER_TOKEN_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + userId));
        userTokenRepository.delete(entity);
    }

    public UserTokenEntity getInfoOfRefreshToken(String refreshToken, String userId) {
        UserTokenEntity entity = userTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.USER_TOKEN_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + userId));
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
