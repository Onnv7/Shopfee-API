package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.dto.common.SizeDto;
import com.hcmute.shopfee.dto.common.ToppingDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class CreateProductRequest {
    @Schema(example = PRODUCT_NAME_EX)
    @NotBlank
    private String name;

    @Schema(example = PRODUCT_PRICE_EX)
    private Long price;

    @Schema(description = NOT_NULL_DES)
    @NotNull
    private MultipartFile image;

    @Schema(description = NOT_EMPTY_DES)
//    @NotEmpty
    private List<SizeDto> sizeList;

    @Schema(example = PRODUCT_DESCRIPTION_EX)
    @NotBlank
    private String description;

    @Schema(description = OPTIONAL_DES)
    private List<ToppingDto> toppingList;

    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private String categoryId;
}
