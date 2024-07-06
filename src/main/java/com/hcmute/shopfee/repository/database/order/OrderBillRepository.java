package com.hcmute.shopfee.repository.database.order;

import com.hcmute.shopfee.dto.sql.GetEmployeeOrderStatisticDto;
import com.hcmute.shopfee.dto.sql.GetStatisticOfOrderQuantityQueryDto;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface OrderBillRepository extends JpaRepository<OrderBillEntity, String> {
    Optional<OrderBillEntity> findByTransaction_Id(String transactionId);

    @Query(value = """
            select ob.*, ob.id as fakeColumns
            from order_bill ob
            join (
            	select order_bill_id, MAX(created_at) as created_at
             	from order_event
             	group by order_bill_id
            ) as last_event on ob.id = last_event.order_bill_id
            join order_event oe on last_event.created_at = oe.created_at
            where oe.order_status = ?1
            order by ob.created_at desc
            """, nativeQuery = true)
    Page<OrderBillEntity> getOrderBillByLastStatus(String status, Pageable pageable) ;

    @Query(value = """
            select ob.*, ob.id as fakeColumns
            from order_bill ob
            join (
            	select order_bill_id, MAX(created_at) as created_at
             	from order_event
             	group by order_bill_id
            ) as last_event on ob.id = last_event.order_bill_id
            join order_event oe on last_event.created_at = oe.created_at
            join (
            	select *
            	from `transaction` t
            	where (t.status = 'PAID' and t.payment_type != 'CASHING')
            	or (t.status = 'UNPAID' and t.payment_type = 'CASHING')
            ) as trans on trans.id = ob.transaction_id
            where oe.order_status = ?1
            and ob.branch_id = ?2
            and ob.order_type = ?3
            AND DATE(ob.created_at) = CURRENT_DATE
            order by ob.created_at desc
                        """, nativeQuery = true)
    Page<OrderBillEntity> getOrderQueueToday(String orderStatus, String branchId, String orderType, Pageable pageable);


    @Query(value = """
            select ob.*, ob.id as fakeColumns
            from order_bill ob
            join(select order_bill_id, MAX(created_at) as created_at
                 from order_event
                 group by order_bill_id) as last_event on ob.id = last_event.order_bill_id
            join order_event oe on last_event.created_at = oe.created_at
            where (oe.order_status = ?1 or ?1 = '')
            order by ob.created_at desc
            """, nativeQuery = true)
    Page<OrderBillEntity> getOrderListForAdmin(String orderStatus, Pageable pageable);

    @Query(value = """
            select ob.*, ob.id as fakeColumns
            from order_bill ob
            join(select order_bill_id, MAX(created_at) as created_at
                 from order_event
                 group by order_bill_id) as last_event on ob.id = last_event.order_bill_id
            join order_event oe on last_event.created_at = oe.created_at
            where ob.user_id = ?2
            and oe.order_status in ?1
            order by ob.created_at desc
            """, nativeQuery = true)
    Page<OrderBillEntity> getOrderListByUserIdAndStatus(List<String> orderStatusList, String userId, Pageable pageable);

    @Query(value = """
            SELECT COUNT(DISTINCT ob.id)
            FROM order_bill AS ob
            JOIN (
                SELECT *
                FROM order_event
                WHERE order_status = ?1
            ) AS oe ON ob.id = oe.order_bill_id
            WHERE DATE(ob.created_at) = ?2
            """, nativeQuery = true)
    long countOrderInCurrentDateByStatus(String orderStatus, String date);

    @Query(value = """
            select
                COUNT(*) AS orderQuantity,
                SUM(CASE WHEN oe.order_status = 'SUCCEED' THEN 1 ELSE 0 END) as succeedOrderQuantity,
                SUM(CASE WHEN oe.order_status = 'CANCELED' THEN 1 ELSE 0 END) as canceledOrderQuantity,
                SUM(CASE WHEN oe.order_status = 'NOT_RECEIVED' THEN 1 ELSE 0 END) as boomOrderQuantity,
                SUM(CASE WHEN oe.order_status = 'CREATED' THEN 1 ELSE 0 END) as pendingOrderQuantity,
                SUM(CASE WHEN oe.order_status IN ('ACCEPTED', 'CANCELLATION_REQUEST', 'CANCELLATION_REQUEST_REFUSED', 'PENDING_PICK_UP', 'IN_DELIVERY') THEN 1 ELSE 0 END) as processingOrderQuantity
            from order_bill ob
            join
            	(select order_bill_id,  MAX(created_at) as created_at
                 from order_event
                 group by order_bill_id) as last_event on ob.id = last_event.order_bill_id
            join order_event oe on last_event.created_at = oe.created_at
            join branch b on b.id = ob.branch_id
            where
            	DATE_FORMAT(ob.created_at, ?2) = DATE_FORMAT(CURRENT_DATE(), ?2)
            	and b.id LIKE concat('%', ?1,'%')
            GROUP BY DATE_FORMAT(ob.created_at, ?2)
            """, nativeQuery = true)
    GetStatisticOfOrderQuantityQueryDto getStatisticOfOrderQuantity(String branchId, String formatTime);


    @Query(value = """
            SELECT DATE_FORMAT(ob.created_at, '%Y-%m-%d') as 'time', sum(ob.total_item_price) as totalItemPrice, count(*) orderCount
            FROM order_bill ob\s
            join order_event oe on ob.id = oe.order_bill_id\s
            JOIN employee e on oe.created_by = e.id\s
            where e.id = ?1\s
            and oe.order_status = 'SUCCEED'
            AND oe.created_at >= ?2
            AND oe.created_at <= ?3
            GROUP BY DATE_FORMAT(ob.created_at, '%Y-%m-%d')
            """, nativeQuery = true)
    List<GetEmployeeOrderStatisticDto> getEmployeeOrderStatistic(String userId, Date startDate, Date endDate);


    @Query(value = """
            select ob.order_type
            from order_bill as ob
            where ob.id = ?1
            """, nativeQuery = true)
    OrderType getOrderType(String orderId);
}
