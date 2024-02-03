package com.hcmute.shopfee.dto.response;


import com.hcmute.shopfee.entity.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import lombok.Data;

import java.util.Date;

@Data
public class GetOrderHistoryForEmployeeResponse {
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

    public static GetOrderHistoryForEmployeeResponse fromOrderBillEntity(OrderBillEntity entity){
        GetOrderHistoryForEmployeeResponse orderResponse = new GetOrderHistoryForEmployeeResponse();
        int lastEventIndex = entity.getOrderEventList().size() - 1;
        orderResponse.setId(entity.getId());
        orderResponse.setCustomerName(entity.getUser().getFullName());
        orderResponse.setPhoneNumber(entity.getUser().getPhoneNumber());
        orderResponse.setProductQuantity(entity.getOrderItemList().size());
        orderResponse.setProductName(entity.getOrderItemList().get(0).getName());
        orderResponse.setProductThumbnailUrl(entity.getOrderItemList().get(0).getProduct().getThumbnailUrl());
        orderResponse.setTimeLastEvent(entity.getOrderEventList().get(lastEventIndex).getCreatedAt());
        orderResponse.setStatusLastEvent(entity.getOrderEventList().get(lastEventIndex).getOrderStatus());
        orderResponse.setTotal(entity.getTotal());
        orderResponse.setOrderType(entity.getOrderType());
        return orderResponse;
    }

}
