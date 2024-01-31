package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.OBJECT_ID_ARRAY_EX;

@Data
public class DeleteSomeProductRequest {
    @Schema(example = OBJECT_ID_ARRAY_EX)
    @NotNull
    private List<String> productIdList;
}
