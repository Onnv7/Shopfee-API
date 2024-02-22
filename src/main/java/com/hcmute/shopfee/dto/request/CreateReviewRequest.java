package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class CreateReviewRequest {
    @Schema(example = OBJECT_ID_EX)
    @NotBlank
    private String orderItemId;

    @Schema(example = RATING_EX)
    @NotBlank
    private Integer star;

    @Schema(example = REVIEW_DES_EX)
    @NotBlank
    private String description;
}
