package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.AddressEntity;
import com.hcmute.shopfee.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, String> {
    List<AddressEntity> findByUser(UserEntity user);
    List<AddressEntity> findByUser_Id(String userId);
}
