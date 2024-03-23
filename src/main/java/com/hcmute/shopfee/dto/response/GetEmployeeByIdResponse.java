package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.entity.sql.database.EmployeeEntity;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.enums.Gender;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetEmployeeByIdResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private Gender gender;
    private java.sql.Date birthDate;
    private String phoneNumber;
    private String email;
    private EmployeeStatus status;
    private String branchName;

    public static GetEmployeeByIdResponse fromEmployeeEntity(EmployeeEntity entity) {
        GetEmployeeByIdResponse data = new GetEmployeeByIdResponse();
        data.setId(entity.getId());
        data.setUsername(entity.getUsername());
        data.setFirstName(entity.getFirstName());
        data.setLastName(entity.getLastName());
        data.setGender(entity.getGender());
        data.setBirthDate(entity.getBirthDate());
        data.setPhoneNumber(entity.getPhoneNumber());
        data.setEmail(entity.getEmail());
        data.setStatus(entity.getStatus());
        data.setBranchName(entity.getBranch() != null ? entity.getBranch().getName() : null);
        return data;
    };
}
