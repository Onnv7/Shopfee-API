package com.hcmute.shopfee.dto.response;


import com.hcmute.shopfee.entity.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import lombok.Data;

import java.util.Date;

@Data
public class GetShippingOrderQueueResponse {
    private String id;
    private String phoneNumber;
    private String productName;
    private int productQuantity;
    private String customerName;
    private String productThumbnailUrl;
    private double total;
    private OrderStatus statusLastEvent;
    private Date timeLastEvent;
    public static GetShippingOrderQueueResponse fromOrderBillEntity(OrderBillEntity orderBillEntity) {
        GetShippingOrderQueueResponse response = new GetShippingOrderQueueResponse();
        int sizeEvent = orderBillEntity.getOrderEventList().size() - 1;
        response.setId(orderBillEntity.getId());
        response.setPhoneNumber(orderBillEntity.getUser().getPhoneNumber());
        response.setCustomerName(orderBillEntity.getUser().getFullName());
        response.setProductName(orderBillEntity.getOrderItemList().get(0).getName());
        response.setProductQuantity(orderBillEntity.getOrderItemList().get(0).getItemDetailList().get(0).getQuantity());
        response.setProductThumbnailUrl(orderBillEntity.getOrderItemList().get(0).getProduct().getThumbnailUrl());
        response.setTimeLastEvent(orderBillEntity.getOrderEventList().get(sizeEvent).getCreatedAt());
        response.setStatusLastEvent(orderBillEntity.getOrderEventList().get(sizeEvent).getOrderStatus());
        response.setTotal(orderBillEntity.getTotalItemPrice());
        return response;
    }
}
