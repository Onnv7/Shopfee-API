package com.hcmute.shopfee.service.redis;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.entity.redis.EmployeeTokenEntity;
import com.hcmute.shopfee.repository.redis.EmployeeTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EmployeeTokenRedisService {
    private final EmployeeTokenRepository employeeTokenRepository;

    public void createNewEmployeeRefreshToken(String refreshToken, String employeeId) {
        EmployeeTokenEntity entity = new EmployeeTokenEntity();
        entity.setEmployeeId(employeeId);
        entity.setRefreshToken(refreshToken);
        entity.setUsed(false);
        employeeTokenRepository.save(entity);
    }

    public void deleteByEmployeeIdAndRefreshToken(String employeeId, String refreshToken) {
        EmployeeTokenEntity entity = employeeTokenRepository.findByEmployeeIdAndRefreshToken(employeeId, refreshToken)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_TOKEN_NOT_FOUND + employeeId));
        employeeTokenRepository.delete(entity);
    }


    public EmployeeTokenEntity getInfoOfRefreshToken(String refreshToken, String employeeId) {
        EmployeeTokenEntity entity = employeeTokenRepository.findByEmployeeIdAndRefreshToken(employeeId, refreshToken)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_TOKEN_NOT_FOUND + employeeId));
        return entity;
    }

    public void updateUsedEmployeeRefreshToken(EmployeeTokenEntity oldValue) {
        oldValue.setUsed(true);
        employeeTokenRepository.save(oldValue);
    }

    public void deleteAllTokenByEmployeeId(String employeeId) {
        List<EmployeeTokenEntity> employeeTokenEntityList = employeeTokenRepository.findByEmployeeId(employeeId);
        for(EmployeeTokenEntity employeeTokenEntity : employeeTokenEntityList) {
            employeeTokenRepository.delete(employeeTokenEntity);
        }
    }
}
