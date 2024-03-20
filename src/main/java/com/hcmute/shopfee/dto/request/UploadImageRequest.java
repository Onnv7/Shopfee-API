package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.enums.AlbumType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class UploadImageRequest {
    @Schema()
    @NotNull
    private MultipartFile image;

    @Schema(example = ALBUM_TYPE_EX)
    @NotNull
    private AlbumType type;
}
