package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.sql.database.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.ProductRewardEntity;
import com.hcmute.shopfee.enums.MoneyRewardUnit;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Data
public class GetRewardOfCouponResponse {
    private List<ProductGift> productRewardList;
    private MoneyReward moneyReward;
    @Data
    public static class  ProductGift {
        private String productName;
        private String productId;
        private String productSize;
        private Short quantity;
        private static ProductGift fromProductRewardEntity(ProductRewardEntity entity) {
            ProductGift data = new ProductGift();
            data.setProductId(entity.getProductId());
            data.setProductSize(entity.getProductSize());
            data.setProductName(entity.getProductName());
            data.setQuantity(entity.getQuantity());
            return data;
        }
    }
    @Data
    public static class MoneyReward {
        private MoneyRewardUnit unit;
        private Integer value;

    }
    public static MoneyReward fromMoneyRewardEntity(MoneyRewardEntity entity) {
        MoneyReward data = new MoneyReward();
        data.setUnit(entity.getUnit());
        data.setValue(entity.getValue());
        return data;
    }
    public static List<ProductGift> fromProductRewardEntityList(List<ProductRewardEntity> entityList) {
        List<ProductGift> data = new ArrayList<>();
        for (ProductRewardEntity entity: entityList) {
            data.add(ProductGift.fromProductRewardEntity(entity));
        }
        return data;
    }
}
