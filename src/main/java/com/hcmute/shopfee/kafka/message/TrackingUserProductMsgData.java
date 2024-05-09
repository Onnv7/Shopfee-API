package com.hcmute.shopfee.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackingUserProductMsgData {
    private String userId;
    private String productId;
}
