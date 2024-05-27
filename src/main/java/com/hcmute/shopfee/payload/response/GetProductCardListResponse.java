package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.dto.common.RatingSummaryDto;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.sql.database.product.BranchProductEntity;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import com.hcmute.shopfee.enums.BranchProductStatus;
import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import lombok.Data;

import java.util.List;

@Data
public class GetProductCardListResponse {
    private Integer totalPage;
    private List<ProductCard> productList;

    @Data
    public static class ProductCard{
        private String id;
        private String name;
        private String description;
        private double price;
        private String thumbnailUrl;
        private BranchProductStatus status;
        private RatingSummaryDto ratingSummary;

        public static ProductCard fromProductEntity( ProductEntity entity, RatingSummaryQueryDto ratingSummaryQueryDto) {
            ProductCard data = new ProductCard();
            data.setId(entity.getId());
            data.setName(entity.getName());
            data.setDescription(entity.getDescription());
            data.setPrice(entity.getPrice());
            data.setThumbnailUrl(entity.getImage().getThumbnailUrl());
//            data.setStatus(entity.getStatus());
//            if(branchId != null) {
//                data.setStatus(entity.getBranchProduct(branchId).getStatus());
//            }
            data.setRatingSummary(RatingSummaryDto.fromRatingSummaryDto(ratingSummaryQueryDto));
            return data;
        }

        public static ProductCard fromProductIndex(ProductIndex entity, RatingSummaryQueryDto ratingSummaryQueryDto) {
            ProductCard data = new ProductCard();
            data.setId(entity.getId());
            data.setName(entity.getName());
            data.setDescription(entity.getDescription());
            data.setPrice(entity.getPrice());
            data.setThumbnailUrl(entity.getThumbnailUrl());
//            data.setStatus(entity.getStatus());
            data.setRatingSummary(RatingSummaryDto.fromRatingSummaryDto(ratingSummaryQueryDto));
            return data;
        }
    }
}
