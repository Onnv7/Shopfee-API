package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.database.AlbumEntity;
import com.hcmute.shopfee.enums.AlbumType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, String> {
    Page<AlbumEntity> findByType(AlbumType type, Pageable pageable);
}
