package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
@Builder
public class RegisterUserRequest {
    @Schema(example = EMAIL_EX)
    @Email
    @NotBlank
    private String email;

    @Schema(example = PASSWORD_EX)
    @NotBlank
    @Size(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    private String password;

    @Schema(example = FIRST_NAME_EX)
    @NotBlank
    private String firstName;

    @Schema(example = LAST_NAME_EX)
    @NotBlank
    private String lastName;

    @Schema(example = PHONE_NUMBER_EX)
    @Pattern(regexp = PHONE_NUMBER_REGEX)
    private String phoneNumber;

}
