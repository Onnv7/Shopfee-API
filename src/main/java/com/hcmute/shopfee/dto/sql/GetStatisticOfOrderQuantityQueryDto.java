package com.hcmute.shopfee.dto.sql;

public interface GetStatisticOfOrderQuantityQueryDto {
    Long getOrderQuantity();
    Long getPendingOrderQuantity();
    Long getProcessingOrderQuantity();
    Long getSucceedOrderQuantity();
    Long getCanceledOrderQuantity();
}
