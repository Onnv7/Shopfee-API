package com.hcmute.shopfee.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingProductMsgData {
    private String userId;
    private String productId;
    private Integer rating;
}
