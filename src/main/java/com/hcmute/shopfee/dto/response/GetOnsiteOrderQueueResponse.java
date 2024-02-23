package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import lombok.Data;

import java.util.Date;

@Data
public class GetOnsiteOrderQueueResponse {
    private String id;
    private String phoneNumber;
    private String customerName;
    private String productName;
    private Integer productQuantity;
    private String productThumbnailUrl;
    private Date receiveTime;
    private Long total;
    private OrderStatus statusLastEvent;
    private Date timeLastEvent;

    public static GetOnsiteOrderQueueResponse fromOrderBillEntity(OrderBillEntity orderBillEntity) {

        GetOnsiteOrderQueueResponse response = new GetOnsiteOrderQueueResponse();
        int sizeEvent = orderBillEntity.getOrderEventList().size() - 1;
        response.setId(orderBillEntity.getId());
        response.setPhoneNumber(orderBillEntity.getUser().getPhoneNumber());
        response.setCustomerName(orderBillEntity.getUser().getFullName());
        response.setProductName(orderBillEntity.getOrderItemList().get(0).getName());
        response.setProductQuantity(orderBillEntity.getOrderItemList().get(0).getItemDetailList().get(0).getQuantity());
        response.setProductThumbnailUrl(orderBillEntity.getOrderItemList().get(0).getProduct().getThumbnailUrl());
        response.setReceiveTime(orderBillEntity.getReceiveTime());
        response.setTotal(orderBillEntity.getTotalItemPrice());
        response.setStatusLastEvent(orderBillEntity.getOrderEventList().get(sizeEvent).getOrderStatus());
        response.setTimeLastEvent(orderBillEntity.getOrderEventList().get(sizeEvent).getCreatedAt());
        return response;
    }
}
