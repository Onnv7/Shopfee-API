package com.hcmute.shopfee.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class ReviewDto {
    @Schema(example = RATING_EX)
    @NotNull
    private double rating;

    @Schema(example = CONTENT_EX)
    private String content;
}
