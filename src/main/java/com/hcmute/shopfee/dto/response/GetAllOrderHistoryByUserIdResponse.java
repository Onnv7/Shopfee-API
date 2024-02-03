package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import lombok.Data;

import java.util.Date;

@Data
public class GetAllOrderHistoryByUserIdResponse {
    private String id;
//    private String code;
    private Long total;
    private int productQuantity;
    private OrderType orderType;
    private String productName;
    private OrderStatus statusLastEvent;
    private Date timeLastEvent;

    public static GetAllOrderHistoryByUserIdResponse fromOrderBillEntity(OrderBillEntity entity) {
        GetAllOrderHistoryByUserIdResponse response = new GetAllOrderHistoryByUserIdResponse();
        int eventLastIndex = entity.getOrderEventList().size() - 1;
        response.setId(entity.getId());
        response.setTotal(entity.getTotal());
        response.setProductQuantity(entity.getOrderItemList().size());
        response.setOrderType(entity.getOrderType());
        response.setProductName(entity.getOrderItemList().get(0).getName());
        response.setStatusLastEvent(entity.getOrderEventList().get(eventLastIndex).getOrderStatus());
        response.setTimeLastEvent(entity.getOrderEventList().get(eventLastIndex).getCreatedAt());
        return response;
    }

}
