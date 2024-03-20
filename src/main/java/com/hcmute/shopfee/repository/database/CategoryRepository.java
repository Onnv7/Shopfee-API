package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.database.CategoryEntity;
import com.hcmute.shopfee.enums.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
    Optional<CategoryEntity> findByName(String name);
    List<CategoryEntity> findByStatus(CategoryStatus status);
}
