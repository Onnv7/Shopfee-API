package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.dto.sql.GetRevenueQueryDto;
import com.hcmute.shopfee.dto.sql.RevenueStatisticsQueryDto;
import com.hcmute.shopfee.entity.database.order.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    @Query(value = """
            select
                SUM(case when YEAR(created_at) = YEAR(?1) AND MONTH(created_at) = MONTH(?1) and DATE(t.created_at) = DATE(?1)  then t.total_paid else 0 end) as revenueByToday,
                SUM(case when YEAR(t.created_at) = YEAR(?1) AND MONTH(created_at) = MONTH(?1) THEN total_paid ELSE 0 END) AS revenueByThisMonth,
                SUM(t.total_paid) as revenue
            from
                `transaction` t
            where
                t.status = 'PAID'
            """, nativeQuery = true)
    GetRevenueQueryDto getRevenueByDate(Date date);

    @Query(value = """
            select sum(t.total_paid) as revenue, DATE_FORMAT(t.created_at, ?3) as time
            from `transaction` t\s
            where t.status = 'PAID'
            AND DATE_FORMAT(t.created_at, '%Y-%m-%d') BETWEEN ?1 AND ?2
            group by DATE_FORMAT(t.created_at, ?3)
            ORDER BY time ASC;
            """, nativeQuery = true)
    List<RevenueStatisticsQueryDto> getRevenueStatistics(java.sql.Date startTime, java.sql.Date endTime, String formatTime);
}
