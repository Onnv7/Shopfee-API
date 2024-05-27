package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.dto.sql.GetStatisticOfOrderQuantityQueryDto;
import lombok.Data;

@Data
public class GetStatisticsOfOrderQuantityResponse {
    private Long orderQuantity;
    private Long pendingOrderQuantity;
    private Long processingOrderQuantity;
    private Long succeedOrderQuantity;
    private Long canceledOrderQuantity;
    private Long boomOrderQuantity;

    public static GetStatisticsOfOrderQuantityResponse fromStatisticOrderQuantityQuery(GetStatisticOfOrderQuantityQueryDto dto) {

        GetStatisticsOfOrderQuantityResponse data = new GetStatisticsOfOrderQuantityResponse();
        if(dto == null) {
            data.setOrderQuantity(0L);
            data.setPendingOrderQuantity(0L);
            data.setProcessingOrderQuantity(0L);
            data.setSucceedOrderQuantity(0L);
            data.setCanceledOrderQuantity(0L);
            data.setBoomOrderQuantity(0L);
        } else {
            data.setOrderQuantity(dto.getOrderQuantity());
            data.setPendingOrderQuantity(dto.getPendingOrderQuantity());
            data.setProcessingOrderQuantity(dto.getProcessingOrderQuantity());
            data.setSucceedOrderQuantity(dto.getSucceedOrderQuantity());
            data.setCanceledOrderQuantity(dto.getCanceledOrderQuantity());
            data.setBoomOrderQuantity(dto.getBoomOrderQuantity());
        }
        return data;
    }
}
