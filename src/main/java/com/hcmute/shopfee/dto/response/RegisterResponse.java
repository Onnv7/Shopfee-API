package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterResponse {
    private String userId;
    private String accessToken;
    @JsonIgnore
    private String refreshToken;
}

