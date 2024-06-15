package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetAllOrderHistoryByUserIdResponse {
    private Integer totalPage;
    private List<OrderHistory> orderList;


    @Data
    public static class OrderHistory {
        private String id;
        private Long total;
        private int productQuantity;
        private OrderType orderType;
        private String productName;
        private OrderStatus statusLastEvent;
        private Date timeLastEvent;
        public static OrderHistory fromOrderBillEntity(OrderBillEntity entity) {
            OrderHistory response = new OrderHistory();

            response.setId(entity.getId());
            response.setTotal(entity.getTotalPayment());
            response.setProductQuantity(entity.getOrderItemList().size());
            response.setOrderType(entity.getOrderType());
            response.setProductName(entity.getOrderItemList().get(0).getName());
            response.setStatusLastEvent(entity.getOrderEventList().get(0).getOrderStatus());
            response.setTimeLastEvent(entity.getOrderEventList().get(0).getCreatedAt());
            return response;
        }
    }

}
