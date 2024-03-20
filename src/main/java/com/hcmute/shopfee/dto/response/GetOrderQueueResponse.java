package com.hcmute.shopfee.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.entity.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GetOrderQueueResponse {
    private Integer totalPage;
    private List<OrderInfo> orderList;
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrderInfo {
        private String id;
        private String phoneNumber;
        private String customerName;
        private String productName;
        private int productQuantity;
        private String productThumbnailUrl;
        private Date receiveTime;
        private double total;
        private OrderStatus statusLastEvent;
        private Date timeLastEvent;
        public static OrderInfo fromOrderBillEntity(OrderBillEntity orderBillEntity) {
            OrderInfo response = new OrderInfo();
            response.setId(orderBillEntity.getId());
            response.setPhoneNumber(orderBillEntity.getShippingInformation().getPhoneNumber());
            response.setCustomerName(orderBillEntity.getUser().getFullName());
            response.setProductName(orderBillEntity.getOrderItemList().get(0).getName());
            response.setProductQuantity(orderBillEntity.getOrderItemList().get(0).getItemDetailList().get(0).getQuantity());
            response.setProductThumbnailUrl(orderBillEntity.getOrderItemList().get(0).getProduct().getImage().getThumbnailUrl());
            response.setReceiveTime(orderBillEntity.getReceiveTime());
            response.setTimeLastEvent(orderBillEntity.getOrderEventList().get(0).getCreatedAt());
            response.setStatusLastEvent(orderBillEntity.getOrderEventList().get(0).getOrderStatus());
            response.setTotal(orderBillEntity.getTotalItemPrice());
            return response;
        }
    }

    public static List<OrderInfo> fromOrderBillEntityList(List<OrderBillEntity> entityList) {
        List<OrderInfo> data = new ArrayList<>();
        for (OrderBillEntity entity : entityList) {
            data.add(OrderInfo.fromOrderBillEntity(entity));
        }
        return data;
    }
}
