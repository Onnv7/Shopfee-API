package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshEmployeeTokenResponse {
    @JsonIgnore
    private String refreshToken;
    private String accessToken;
}
