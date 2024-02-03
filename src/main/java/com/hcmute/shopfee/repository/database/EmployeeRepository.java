package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {
    Optional<EmployeeEntity> findByUsernameAndIsDeletedFalse(String username);
    Optional<EmployeeEntity> findByIdAndIsDeletedFalse(String username);

    @Query(value = """
            select e.id, e.birth_date, e.created_at, e.first_name, e.last_name, e.gender, e.is_deleted, e.password, e.phone_number, e.status, e.update_at, e.username, e.branch_id\s
            from employee e
            where e.status regexp ?1
            and is_deleted = false
            """, nativeQuery = true)
    Page<EmployeeEntity> getEmployeeList(String statusRegex, Pageable pageable);

    @Query(value = """
            select e.id, e.birth_date, e.created_at, e.first_name, e.last_name, e.gender, e.is_deleted, e.password, e.phone_number, e.status, e.update_at, e.username, e.branch_id\s
            from employee e
            where is_deleted = false
            and status regexp  ?2
            and username regexp ?1
            """, nativeQuery = true)
    Page<EmployeeEntity> searchEmployee(String key, String status, Pageable pageable);
}
