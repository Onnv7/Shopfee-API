package com.hcmute.shopfee.entity.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("employee_token")
@Data
public class EmployeeTokenEntity {
    @Id
    private String id;
    @Indexed
    private String employeeId;
    @Indexed
    private String refreshToken;
    private boolean isUsed;
}
