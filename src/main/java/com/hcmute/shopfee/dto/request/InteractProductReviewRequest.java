package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.enums.ReviewInteraction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class InteractProductReviewRequest {
    @Schema(example = INTERACTION)
    @NotNull
    private ReviewInteraction interaction;

    @Schema(example = OBJECT_ID_EX)
    @NotBlank
    private String userId;
}
