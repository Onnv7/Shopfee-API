package com.hcmute.shopfee.dto.common;

import com.hcmute.shopfee.dto.response.GetProductViewByIdResponse;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import lombok.Data;

@Data
public class RatingSummaryDto {
    private double star;
    private long quantity;

    public static RatingSummaryDto fromRatingSummaryDto(RatingSummaryQueryDto dto) {
        RatingSummaryDto data = new RatingSummaryDto();
        data.setStar(dto.getStar());
        data.setQuantity(dto.getQuantity());
        return data;
    }
}
