package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.enums.CouponType;
import com.hcmute.shopfee.enums.UsageConditionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CheckCouponInCartResponse {
    public CheckCouponInCartResponse() {
        isValid = true;
    }
    private CouponType couponType;
    private boolean isValid;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MinPurchaseCondition minPurchaseCondition;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<UsageCondition> usageConditionList;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SubjectCondition> subjectConditionList;

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
