package com.hcmute.shopfee.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeEmailMsgData {
    private String code;
    private String email;
}
