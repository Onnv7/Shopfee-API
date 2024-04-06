package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.REASON_EX;

@Data
public class CreateOrderReturnRequest {
    @Schema(example = REASON_EX)
    @NotBlank
    private String reason;

    @NotEmpty
    private List<MultipartFile> mediaList;
}
