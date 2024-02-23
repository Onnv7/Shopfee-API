package com.hcmute.shopfee.entity.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


@RedisHash("employee_token")
@Data
@Builder
public class UserTokenEntity {
    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String refreshToken;
    private boolean isUsed;
}
