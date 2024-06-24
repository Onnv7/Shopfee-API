package com.hcmute.shopfee.dto.common;

import lombok.Data;

@Data
public class UserNode {
    private String userId;
    private String gender;

    public UserNode(String userId, String gender) {
        this.userId = userId;
        this.gender = gender;
    }
}
