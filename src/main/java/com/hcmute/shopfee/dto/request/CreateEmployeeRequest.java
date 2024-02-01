package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class CreateEmployeeRequest {
    @Schema(example = USERNAME_EMPLOYEE_EX)
    @NotBlank
    private String username;

    @Schema(example = PASSWORD_EMPLOYEE_EX)
    @NotBlank
    @Size(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    private String password;

    @Schema(example = FIRST_NAME_EMPLOYEE_EX)
    @NotBlank
    private String firstName;

    @Schema(example = LAST_NAME_EMPLOYEE_EX)
    @NotBlank
    private String lastName;

    @Schema(example = BIRTH_DATE_EX)
    @NotNull
    private Date birthDate;

    @Schema(example = GENDER_EX)
    @NotNull
    private Gender gender;

    @Schema(example = OBJECT_ID_EX)
    @NotBlank
    private String branchId;
}
