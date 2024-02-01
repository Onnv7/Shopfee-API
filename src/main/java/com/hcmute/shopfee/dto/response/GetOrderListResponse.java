package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetOrderListResponse {
    private List<Order> orderList;
    private Integer totalPage;
    @Data
    public static class Order {
        private String id;
        private String code;
        private Date createdAt;
        private OrderType orderType;
        private String customerName;
        private Long total;
        private OrderStatus statusLastEvent;

        public static Order fromOrderBillEntity(OrderBillEntity orderBillEntity) {
            Order order = new Order();
            int eventLastIndex = orderBillEntity.getOrderEventList().size() - 1;
            order.setId(orderBillEntity.getId());
            order.setCreatedAt(orderBillEntity.getCreatedAt());
            order.setOrderType(orderBillEntity.getOrderType());
            order.setCustomerName(orderBillEntity.getUser().getFullName());
            order.setTotal(orderBillEntity.getTotal());
            order.setStatusLastEvent(orderBillEntity.getOrderEventList().get(eventLastIndex).getOrderStatus());
            return order;
        }
    }
}
