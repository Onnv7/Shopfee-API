package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.shopfee.entity.order.OrderEventEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import lombok.Data;

import java.util.Date;

@Data
public class GetOrderStatusLineResponse {
    private OrderStatus orderStatus;
    private Date createdAt;
    private String description;
//    @JsonProperty("makerByEmployee")
    private boolean isEmployee;

    public static GetOrderStatusLineResponse fromOrderEventEntity(OrderEventEntity entity){
        GetOrderStatusLineResponse response = new GetOrderStatusLineResponse();
        response.setOrderStatus(entity.getOrderStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setDescription(entity.getDescription());
        response.setEmployee(entity.isEmployee());
        return  response;
    }
}
