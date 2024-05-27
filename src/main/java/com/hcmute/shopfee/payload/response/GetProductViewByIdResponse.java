package com.hcmute.shopfee.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.common.SizeDto;
import com.hcmute.shopfee.dto.common.ToppingDto;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.enums.BranchProductStatus;
import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetProductViewByIdResponse {
    private String id;
    private ProductType type;
    private String name;
    private String imageUrl;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SizeDto> sizeList;
    private String description;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ToppingDto> toppingList;
    private BranchProductStatus status;
    private Long price;
    private RatingSummaryDto ratingSummary;
}
