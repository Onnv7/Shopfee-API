package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.sql.GetStatisticOfOrderQuantityQueryDto;
import lombok.Data;

@Data
public class GetStatisticsOfOrderQuantityResponse {
    private Long orderQuantity;
    private Long pendingOrderQuantity;
    private Long processingOrderQuantity;
    private Long succeedOrderQuantity;
    private Long canceledOrderQuantity;

    public static GetStatisticsOfOrderQuantityResponse fromStatisticOrderQuantityQuery(GetStatisticOfOrderQuantityQueryDto dto) {
        GetStatisticsOfOrderQuantityResponse data = new GetStatisticsOfOrderQuantityResponse();
        data.setOrderQuantity(dto.getOrderQuantity());
        data.setPendingOrderQuantity(dto.getPendingOrderQuantity());
        data.setProcessingOrderQuantity(dto.getProcessingOrderQuantity());
        data.setSucceedOrderQuantity(dto.getSucceedOrderQuantity());
        data.setCanceledOrderQuantity(dto.getCanceledOrderQuantity());
        return data;
    }
}
