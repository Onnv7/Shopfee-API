package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.admin.BranchEntity;
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
            select b.*, b.id as fakeColumns
            from branch b\s
            where b.status = ?1
            and(concat_ws(' ', b.detail, b.ward, b.district, b.province, b.name)  LIKE concat('%', ?2,'%') OR ?2 = '')
            """, nativeQuery = true)
    Page<BranchEntity> getBranchByStatusAndKey(String status, String key, Pageable pageable);
    Page<BranchEntity> findByStatus(BranchStatus status, Pageable pageable);
    List<BranchEntity> findByStatus(BranchStatus status);
    Optional<BranchEntity> findByIdAndStatus(String id, BranchStatus status);

    @Query(value = """
            SELECT count(*)
            FROM branch b
            join order_bill ob on ob.branch_id = b.id
            where b.id = ?1
            """, nativeQuery = true)
    int countOrderBillByBranch(String branchId);

    @Query(value = """
            SELECT count(*)
            FROM branch b
            join employee e on e.branch_id = b.id
            where b.id = ?1
            """, nativeQuery = true)
    int countEmployeeByBranch(String branchId);

}
