package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.EmployeeFCMTokenEntity;
import com.hcmute.shopfee.entity.sql.database.UserFCMTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeFCMTokenRepository extends JpaRepository<EmployeeFCMTokenEntity, String> {
    List<EmployeeFCMTokenEntity> findByEmployeeId(String employeeId);
    Optional<EmployeeFCMTokenEntity> findByToken(String fmcToken);
}
