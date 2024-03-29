package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.common.SizeDto;
import com.hcmute.shopfee.dto.common.ToppingDto;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.enums.ProductStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class GetProductViewByIdResponse {
    @Id
    private String id;
    private String name;
    private String imageUrl;
    private List<SizeDto> sizeList;
    private String description;
    private List<ToppingDto> toppingList;
    private ProductStatus status;
    private RatingSummaryDto ratingSummary;
}
