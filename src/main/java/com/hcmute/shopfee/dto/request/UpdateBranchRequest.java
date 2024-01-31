package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class UpdateBranchRequest {
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
}
