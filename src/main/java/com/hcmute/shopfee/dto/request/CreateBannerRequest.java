package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class CreateBannerRequest {
    @Schema(example = BANNER_NAME_EX)
    @NotBlank
    private String name;

    @Schema(description = NOT_NULL_DES)
    @NotNull
    private MultipartFile image;
}
