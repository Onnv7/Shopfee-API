package com.hcmute.shopfee.payload.request;

import com.hcmute.shopfee.enums.BlogStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class UpdateBlogRequest {
    @Schema(example = BLOG_STATUS_EX)
    @NotNull
    private BlogStatus status;

    @Schema(example = TITLE_NOTI_EX)
    @NotBlank
    private String title;

    @Schema(example = BLOG_SUMMARY_EX)
    @NotBlank
    private String summary;

    @Schema(example = CONTENT_EX)
    @NotBlank
    private String content;

    @Schema()
    private MultipartFile image;
}
