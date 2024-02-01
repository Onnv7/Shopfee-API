package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    @Query(value = """
            select sum(t.total_paid)
            from `transaction` t
            WHERE DATE(t.update_at) = ?1
            """, nativeQuery = true)
    long getRevenueAt(String date);
}
