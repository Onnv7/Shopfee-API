package com.hcmute.shopfee.repository.redis;

import com.hcmute.shopfee.entity.redis.EmployeeTokenEntity;
import com.hcmute.shopfee.entity.redis.UserTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserTokenRepository extends CrudRepository<UserTokenEntity, String> {
    Optional<UserTokenEntity> findByUserIdAndRefreshToken(String userId, String refreshToken);
    List<UserTokenEntity> findByUserId(String userId);
}
