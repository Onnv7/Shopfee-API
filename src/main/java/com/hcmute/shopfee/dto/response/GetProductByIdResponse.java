package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.dto.common.SizeDto;
import com.hcmute.shopfee.dto.common.ToppingDto;
import com.hcmute.shopfee.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class GetProductByIdResponse {
    private String id;
    private String name;
    private List<SizeDto> sizeList;
    private String description;
    private List<ToppingDto> toppingList;
    private String categoryId;
    private ProductStatus status;
    private String imageUrl;

}
