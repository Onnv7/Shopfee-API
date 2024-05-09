package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.enums.Gender;
import com.hcmute.shopfee.enums.UserStatus;
import lombok.Data;

import java.util.Date;

@Data
public class GetUserDetailsByIdResponse {
    private String id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date birthDate;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private Long coin;
    private UserStatus status;
}
