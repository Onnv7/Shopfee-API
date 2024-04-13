package com.hcmute.shopfee.module.zalopay.refund.dto.request;

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
