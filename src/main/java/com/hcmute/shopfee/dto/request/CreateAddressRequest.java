package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class CreateAddressRequest {
    @Schema(example = CATEGORY_NAME_EX)
    @NotBlank
    private String detail;

    @Schema(example = LONGITUDE_EX)
    @NotNull
    private double longitude;

    @Schema(example = LATITUDE_EX)
    @NotNull
    private double latitude;

    @Schema(example = ADDRESS_NOTE_EX)
    private String note;

    @Schema(example = RECIPIENT_NAME_EX)
    @NotBlank
    private String recipientName;

    @Schema(example = PHONE_NUMBER_EX)
    @NotBlank
    @Pattern(regexp = PHONE_NUMBER_REGEX)
    private String phoneNumber;

}
