package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.database.BranchEntity;
import com.hcmute.shopfee.enums.BranchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, String> {

    @Query(value = """
            select b.id, b.close_time, b.created_at, b.detail, b.district, b.image_id, b.image_url, b.latitude, b.longitude, b.name, b.open_time, b.phone_number, b.province, b.status, b.updated_at, b.ward\s
            from branch b\s
            where b.status = ?1
            and(concat_ws(' ', b.detail, b.ward, b.district, b.province, b.name)  LIKE concat('%', ?2,'%') OR ?2 = '')
            """, nativeQuery = true)
    Page<BranchEntity> getBranchByStatusAndKey(String status, String key, Pageable pageable);
    Page<BranchEntity> findByStatus(BranchStatus status, Pageable pageable);
    List<BranchEntity> findByStatus(BranchStatus status);
    Optional<BranchEntity> findByIdAndStatus(String id, BranchStatus status);
}
