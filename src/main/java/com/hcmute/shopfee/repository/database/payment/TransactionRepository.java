package com.hcmute.shopfee.repository.database.payment;

import com.hcmute.shopfee.dto.sql.GetRevenueQueryDto;
import com.hcmute.shopfee.dto.sql.GetStatisticByKeyValue;
import com.hcmute.shopfee.dto.sql.GetUserSpendingStatisticDto;
import com.hcmute.shopfee.dto.sql.RevenueStatisticsQueryDto;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
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

    @Query(value = """
            SELECT DATE_FORMAT(ob.created_at, '%Y-%m-%d') AS time, SUM(t.total_paid) as amount
            FROM `transaction` t\s
            JOIN order_bill ob ON ob.id = t.order_bill_id\s
            JOIN `user` u ON ob.user_id = u.id\s
            WHERE u.id = ?3\s
            AND t.status = 'PAID'
            AND t.created_at >= ?1
            AND t.created_at <= ?2
            GROUP BY DATE_FORMAT(ob.created_at, '%Y-%m-%d');
            """, nativeQuery = true)
    List<GetUserSpendingStatisticDto> getUserSpendingStatisticsByDate(java.sql.Date dateStart, java.sql.Date dateEnd, String userId);

    @Query(value = """
            
            SELECT sample.payment_type as 'key', COALESCE(statistic.value, 0) as value
            FROM (
            	SELECT 'ZALOPAY' AS payment_type
            	UNION ALL
            	SELECT 'VNPAY' AS payment_type
            	UNION ALL
            	SELECT 'CASHING' AS payment_type
            	) AS sample
            left join (
            	SELECT t.payment_type, sum(t.total_paid) as value
            	FROM `transaction` t\s
            	join order_bill ob on t.order_bill_id = ob.id\s
            	join `user` u on u.id = ob.user_id\s
            	WHERE u.id = ?1
            	and t.status = 'PAID'
            	GROUP BY t.payment_type\s
            ) as statistic on statistic.payment_type = sample.payment_type
            """, nativeQuery = true)
    List<GetStatisticByKeyValue> getUserPaymentTypeStatistic(String userId);


}
