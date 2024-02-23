package com.hcmute.shopfee.repository.redis;

import com.hcmute.shopfee.entity.redis.EmployeeTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeTokenRepository extends CrudRepository<EmployeeTokenEntity, String> {
    Optional<EmployeeTokenEntity> findByEmployeeIdAndRefreshToken(String employeeId, String refreshToken);
    List<EmployeeTokenEntity> findByEmployeeId(String employeeId);
}
