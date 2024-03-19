package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class UpdateEmployeeProfileRequest {
    @Schema(example = BIRTH_DATE_EX)
    @NotNull
    private Date birthDate;

    @Schema(example = GENDER_EX)
    @NotNull
    private Gender gender;

    @Schema(example = PHONE_NUMBER_EX)
    @NotBlank
    @Pattern(regexp = PHONE_NUMBER_REGEX)
    private String phoneNumber;

    @Schema(example = EMAIL_EX)
    @NotBlank
    private String email;
}
