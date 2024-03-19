package com.hcmute.shopfee.dto.response;


import com.hcmute.shopfee.entity.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GetOrderHistoryForEmployeeResponse {
    private Integer totalPage;
    private List<OrderInfo> orderList;

    @Data
    public static class OrderInfo {
        private String id;
        //    private String code;
        private String customerName;
        private String phoneNumber;
        private String productName;
        private int productQuantity;
        private String productThumbnailUrl;
        private Date timeLastEvent;
        private Long total;
        private OrderStatus statusLastEvent;
        private OrderType orderType;

        public static OrderInfo fromOrderBillEntity(OrderBillEntity entity){
            OrderInfo orderResponse = new OrderInfo();

            orderResponse.setId(entity.getId());
            orderResponse.setCustomerName(entity.getUser().getFullName());
            orderResponse.setPhoneNumber(entity.getShippingInformation().getPhoneNumber());
            orderResponse.setProductQuantity(entity.getOrderItemList().size());
            orderResponse.setProductName(entity.getOrderItemList().get(0).getName());
            orderResponse.setProductThumbnailUrl(entity.getOrderItemList().get(0).getProduct().getThumbnailUrl());
            orderResponse.setTimeLastEvent(entity.getOrderEventList().get(0).getCreatedAt());
            orderResponse.setStatusLastEvent(entity.getOrderEventList().get(0).getOrderStatus());
            orderResponse.setTotal(entity.getTotalItemPrice());
            orderResponse.setOrderType(entity.getOrderType());
            return orderResponse;
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
