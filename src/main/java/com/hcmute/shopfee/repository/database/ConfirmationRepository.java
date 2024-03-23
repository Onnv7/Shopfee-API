package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.ConfirmationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, String> {
    Optional<ConfirmationEntity> findByEmail(String email);
    Optional<ConfirmationEntity> findByEmailAndCode(String email, String code);
}
