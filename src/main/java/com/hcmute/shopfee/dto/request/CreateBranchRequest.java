package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Time;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class CreateBranchRequest {
    @Schema()
    @NotNull
    private MultipartFile image;

    @Schema(example = PROVINCE_EX)
    @NotBlank
    private String province;

    @Schema(example = STORE_NAME_EX)
    @NotBlank
    private String name;

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

    @Schema(example = OPEN_TIME_EX)
    @NotNull
    private Time openTime;

    @Schema(example = CLOSE_TIME_EX)
    @NotNull
    private Time closeTime;

    @Schema(example = PHONE_NUMBER_EX)
    @NotBlank
    @Pattern(regexp = PHONE_NUMBER_REGEX)
    private String phoneNumber;
}
