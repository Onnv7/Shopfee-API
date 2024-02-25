package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.enums.Gender;
import lombok.Data;

import java.util.Date;


@Data
public class GetEmployeeProfileByIdResponse {
    private String username;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date birthDate;
    private String phoneNumber;
}
