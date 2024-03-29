package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
import lombok.Data;

import java.util.Date;

@Data
public class GetOrderStatusLineResponse {
    private OrderStatus orderStatus;
    private Date createdAt;
    private String description;
//    @JsonProperty("makerByEmployee")
    private ActorType actor;

    public static GetOrderStatusLineResponse fromOrderEventEntity(OrderEventEntity entity){
        GetOrderStatusLineResponse response = new GetOrderStatusLineResponse();
        response.setOrderStatus(entity.getOrderStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setDescription(entity.getDescription());
        response.setActor(entity.getActor());
        return  response;
    }
}
