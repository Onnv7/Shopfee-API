package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class CreateBranchRequest {

    @Schema(example = PROVINCE_EX)
    @NotBlank
    private String province;


    @Schema(example = DISTRICT_EX)
    @NotBlank
    private String district;

    @Schema(example = WARD_EX)
    @NotBlank
    private String ward;

    @Schema(example = ADDRESS_DETAILS_EX)
    @NotBlank
    private String detail;

    @Schema(example = LONGITUDE_EX)
    @NotNull
    private double longitude;

    @Schema(example = LATITUDE_EX)
    @NotNull
    private double latitude;

    @Schema(example = PHONE_NUMBER_EX)
    @NotBlank
    @Pattern(regexp = PHONE_NUMBER_REGEX)
    private String phoneNumber;
}
