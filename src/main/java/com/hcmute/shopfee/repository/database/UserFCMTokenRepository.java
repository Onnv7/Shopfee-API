package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.UserFCMTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFCMTokenRepository extends JpaRepository<UserFCMTokenEntity, String> {
    List<UserFCMTokenEntity> findByUser_Id(String userId);
}
