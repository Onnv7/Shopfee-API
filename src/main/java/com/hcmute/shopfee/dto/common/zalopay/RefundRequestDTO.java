package com.hcmute.shopfee.dto.common.zalopay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDTO {

    private String zpTransId;
    private Long amount;
    private String description;
}
