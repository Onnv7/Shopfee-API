package com.hcmute.shopfee.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenResponse {
    private String refreshToken;
    private String accessToken;
}
