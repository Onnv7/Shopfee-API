package com.hcmute.shopfee.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private final String userId;
    private final String accessToken;
    private final String refreshToken;
}
