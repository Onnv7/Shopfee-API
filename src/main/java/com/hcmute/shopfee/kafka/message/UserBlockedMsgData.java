package com.hcmute.shopfee.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBlockedMsgData {
    private String userName;
    private String email;
}
