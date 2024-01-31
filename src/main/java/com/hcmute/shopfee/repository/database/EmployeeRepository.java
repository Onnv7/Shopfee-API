package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {

    Optional<EmployeeEntity> findByUsername(String username);
}
