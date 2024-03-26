package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.order.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, String> {
    List<OrderItemEntity> findByOrderBill_Id(String orderBillId);

    @Query(value = """
            select count(*)
            from order_item oi\s
            where oi.image_url = ?1
            """, nativeQuery = true)
    long countOrderItemByImageUrl(String imageUrl);
}
