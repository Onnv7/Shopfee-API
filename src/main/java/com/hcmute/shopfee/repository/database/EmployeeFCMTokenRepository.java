package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.EmployeeFCMTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeFCMTokenRepository extends JpaRepository<EmployeeFCMTokenEntity, String> {
    List<EmployeeFCMTokenEntity> findByEmployeeId(String employeeId);
}
