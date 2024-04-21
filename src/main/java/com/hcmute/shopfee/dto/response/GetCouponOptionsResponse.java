package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.enums.CouponType;
import com.hcmute.shopfee.enums.UsageConditionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GetCouponOptionsResponse {
    private List<CouponType> shippingNoCombineBy;
    private List<CouponCard> shippingCouponList;
    private List<CouponType> orderNoCombineBy;
    private List<CouponCard> orderCouponList;
    private List<CouponType> productNoCombineBy;
    private List<CouponCard> productCouponList;

    public GetCouponOptionsResponse() {
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CouponCard{
        public CouponCard() {
        }
        private String couponId;
        private String code;
        private String description;
        private Date expirationDate;
        private boolean isValid;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private MinPurchaseCondition minPurchaseCondition;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<UsageCondition> usageConditionList;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<SubjectCondition> subjectConditionList;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<CombinationCondition> combinationConditionList;
    }
    @Data
    public static class MinPurchaseCondition {
        private Long value;
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
    @Data
    public static class CombinationCondition {
        public CombinationCondition(CouponType type) {
            this.type = type;
        }

        public CombinationCondition() {
        }

        private CouponType type;
    }
}
