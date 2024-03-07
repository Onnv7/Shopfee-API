package com.hcmute.shopfee.repository.database.order;

import com.hcmute.shopfee.dto.sql.GetStatisticOfOrderQuantityQueryDto;
import com.hcmute.shopfee.entity.database.order.OrderBillEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OrderBillRepository extends JpaRepository<OrderBillEntity, String> {
    Optional<OrderBillEntity> findByTransaction_Id(String transactionId);

    @Query(value = """
            select ob.id, ob.created_at, ob.note, ob.order_type, ob.receive_time, ob.shipping_fee, ob.total, ob.updated_at, ob.branch_id, ob.user_id
            from order_bill ob
            join (
            	select *
            	from order_event
            	where order_event.order_status = ?1
            ) as oe on ob.id = oe.order_bill_id
            order by ob.created_at desc
            """, nativeQuery = true)
    Page<OrderBillEntity> getOrderBillByLastStatus(String status, Pageable pageable) ;

    @Query(value = """
            select ob.id, ob.created_at, ob.note, ob.order_type, ob.receive_time, ob.shipping_fee, ob.total, ob.updated_at, ob.branch_id, ob.user_id
            from order_bill ob
            join (
            	select *
            	from order_event
            	where order_event.order_status = ?1
            ) as oe on ob.id = oe.order_bill_id
            where ob.branch_id = ?2
            and ob.order_type = ?3
            AND DATE(ob.created_at) = CURRENT_DATE
            order by ob.created_at desc
            """, nativeQuery = true)
    Page<OrderBillEntity> getShippingOrderQueueToday(String orderStatus, Long branchId, String orderType, Pageable pageable);


    @Query(value = """
            select  ob.id, ob.created_at, ob.note, ob.order_type, ob.receive_time, ob.shipping_fee, ob.total, ob.updated_at, ob.branch_id, ob.user_id
            from order_bill AS ob
            join (
            	select *
            	from order_event
            	where order_event.order_status regexp ?1
            ) as oe on ob.id = oe.order_bill_id
            order by ob.created_at desc
            """, nativeQuery = true)
    Page<OrderBillEntity> getOrderList(String orderStatusRegex, Pageable pageable);

    @Query(value = """
            select ob.id, ob.created_at, ob.note, ob.order_type, ob.receive_time, ob.shipping_fee, ob.total, ob.updated_at, ob.branch_id, ob.user_id
            from order_bill ob
            join (
            	select *
            	from order_event
            	where order_event.order_status = ?1
            ) as oe on ob.id = oe.order_bill_id
            where ob.user_id = ?2
            order by ob.created_at desc
            """, nativeQuery = true)
    Page<OrderBillEntity> getOrderListByUserIdAndStatus(String orderStatus, String userId, Pageable pageable);

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
            select\s
                COUNT(*) AS orderQuantity,
                SUM(CASE WHEN oe.order_status  = 'SUCCEED' THEN 1 ELSE 0 END) as succeedOrderQuantity,
                SUM(CASE WHEN oe.order_status = 'CANCELED' THEN 1 ELSE 0 END) as canceledOrderQuantity,
                SUM(CASE WHEN oe.order_status = 'CREATED' THEN 1 ELSE 0 END) as pendingOrderQuantity,
                SUM(CASE WHEN oe.order_status IN ('ACCEPTED', 'DELIVERING') THEN 1 ELSE 0 END) as processingOrderQuantity
                from order_bill ob
            join
            	(select order_bill_id,  MAX(created_at) as created_at
                 from order_event
                 group by order_bill_id) as last_event on ob.id = last_event.order_bill_id
            join order_event oe on last_event.created_at = oe.created_at
            """, nativeQuery = true)
    GetStatisticOfOrderQuantityQueryDto getStatisticOfOrderQuantity();
}
