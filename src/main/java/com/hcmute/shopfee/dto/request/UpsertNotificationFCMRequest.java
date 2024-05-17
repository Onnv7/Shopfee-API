package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class UpsertNotificationFCMRequest {
    @Schema(example = TITLE_NOTI_EX)
    @NotBlank
    private String title;

    @Schema(example = CONTENT_NOTI_EX)
    @NotBlank
    private String content;

    @Schema(example = IMAGE_URL_NOTI_EX)
    private String imageUrl;

    @Schema(example = DATE_ISO_EX)
    private Date triggerTime;
}
