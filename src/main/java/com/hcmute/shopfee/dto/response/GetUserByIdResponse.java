package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.enums.Gender;
import lombok.Data;

import java.util.Date;

@Data
public class GetUserByIdResponse {
    private String id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date birthDate;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private Long coin;
}
