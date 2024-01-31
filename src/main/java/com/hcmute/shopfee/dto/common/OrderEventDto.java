package com.hcmute.shopfee.dto.common;

import com.hcmute.shopfee.enums.OrderStatus;
import lombok.Data;

import java.util.Date;

@Data
public class OrderEventDto {
    private OrderStatus orderStatus;
    private Date time;
    private String description;
//    private ObjectId makerId;
    private boolean isEmployee;
}
