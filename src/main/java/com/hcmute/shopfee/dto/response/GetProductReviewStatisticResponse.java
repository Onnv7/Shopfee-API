package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.sql.GetProductReviewStatisticDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetProductReviewStatisticResponse {
    private int reviewCountTotal;
    private List<ItemStar> statistics;
    public static GetProductReviewStatisticResponse fromGetProductReviewStatisticResponseList(List<GetProductReviewStatisticDto> dtoList) {
        GetProductReviewStatisticResponse data = new GetProductReviewStatisticResponse();
        int reviewCountTotal = 0;
        List<ItemStar> statistics = new ArrayList<ItemStar>();
        for(GetProductReviewStatisticDto dto : dtoList) {
            ItemStar itemStar = new ItemStar();
            itemStar.setStar(dto.getStarRating());
            itemStar.setCount(dto.getCount());
            statistics.add(itemStar);
            reviewCountTotal += dto.getCount();
        }
        data.setStatistics(statistics);
        data.setReviewCountTotal(reviewCountTotal);
        return data;
    }
    @Data
    public static class ItemStar {
        private int star;
        private int count;
    }
}
