package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import lombok.Data;

import java.util.Date;

@Data
public class GetAllOrderHistoryByUserIdResponse {
    private String id;
    private Long total;
    private int productQuantity;
    private OrderType orderType;
    private String productName;
    private OrderStatus statusLastEvent;
    private Date timeLastEvent;

    public static GetAllOrderHistoryByUserIdResponse fromOrderBillEntity(OrderBillEntity entity) {
        GetAllOrderHistoryByUserIdResponse response = new GetAllOrderHistoryByUserIdResponse();

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
