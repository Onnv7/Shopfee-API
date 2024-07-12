package com.hcmute.shopfee.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.ProductRewardEntity;
import com.hcmute.shopfee.enums.CouponType;
import com.hcmute.shopfee.enums.MoneyRewardUnit;
import com.hcmute.shopfee.enums.UsageConditionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CheckCouponInCartResponse {
    public CheckCouponInCartResponse() {
        isValid = true;
    }
    private CouponType couponType;
    private boolean isValid;
    private ViolatedCondition violatedCondition;
    private Reward reward;


    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Reward {
        private List<CheckCouponInCartResponse.ProductGift> productRewardList;
        private CheckCouponInCartResponse.MoneyReward moneyReward;
        private List<SubjectInformation> subjectInformationList;
    }

    @Data
    public static class ViolatedCondition {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private MinPurchaseCondition minPurchaseCondition;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<UsageCondition> usageConditionList;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<SubjectCondition> subjectConditionList;
    }



    @Data
    public static class SubjectInformation {
        private String id;
        private String name;

        public SubjectInformation(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    @Data
    public static class  ProductGift {
        private String productName;
        private String productId;
        private String productSize;
        private Short quantity;
        private static CheckCouponInCartResponse.ProductGift fromProductRewardEntity(ProductRewardEntity entity) {
            CheckCouponInCartResponse.ProductGift data = new CheckCouponInCartResponse.ProductGift();
            data.setProductId(entity.getId().getProductId());
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
    public static CheckCouponInCartResponse.MoneyReward fromMoneyRewardEntity(MoneyRewardEntity entity) {
        CheckCouponInCartResponse.MoneyReward data = new CheckCouponInCartResponse.MoneyReward();
        data.setUnit(entity.getUnit());
        data.setValue(entity.getValue());
        return data;
    }
    public static List<CheckCouponInCartResponse.ProductGift> fromProductRewardEntityList(List<ProductRewardEntity> entityList) {
        List<CheckCouponInCartResponse.ProductGift> data = new ArrayList<>();
        for (ProductRewardEntity entity: entityList) {
            data.add(CheckCouponInCartResponse.ProductGift.fromProductRewardEntity(entity));
        }
        return data;
    }
    @Data
    public static class MinPurchaseCondition {
        private Long value;

        public MinPurchaseCondition() {
        }

        public MinPurchaseCondition(Long value) {
            this.value = value;
        }
    }

    @Data
    public static class UsageCondition {
        public UsageCondition(UsageConditionType type, Integer value) {
            this.type = type;
            this.value = value;
        }

        public UsageCondition() {
        }

        private UsageConditionType type;
        private Integer value;
    }

    @Data
    public static class SubjectCondition {
        public SubjectCondition(String productName, Integer value) {
            this.productName = productName;
            this.value = value;
        }

        private String productName;
        private Integer value;
    }
}
