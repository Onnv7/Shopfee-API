package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.AddressEntity;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, String> {
    List<AddressEntity> findByUser(UserEntity user);
    List<AddressEntity> findByUserIdOrderByIsDefaultDesc(String userId);
}
