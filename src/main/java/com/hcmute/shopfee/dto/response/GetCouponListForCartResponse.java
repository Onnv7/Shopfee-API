package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.dto.common.coupon.condition.CombinationConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.MinPurchaseConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.SubjectConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.UsageConditionDto;
import com.hcmute.shopfee.enums.CouponType;
import com.hcmute.shopfee.enums.UsageConditionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.COUPON_QUANTITY_EX;
import static com.hcmute.shopfee.constant.SwaggerConstant.INTEGER_VALUE_EX;

@Data
public class GetCouponListForCartResponse {
    private boolean canCombinedWithShippingCoupon;
    private List<CouponCard> shippingCouponList;
    private boolean canCombinedWithOrderCoupon;
    private List<CouponCard> orderCouponList;
    private boolean canCombinedWithProductCoupon;
    private List<CouponCard> productCouponList;

    public GetCouponListForCartResponse() {
        canCombinedWithShippingCoupon = true;
        canCombinedWithOrderCoupon = true;
        canCombinedWithProductCoupon = true;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CouponCard{
        public CouponCard() {
            minPurchaseCondition = new MinPurchaseCondition();
            usageConditionList = new ArrayList<>();
            subjectConditionList = new ArrayList<>();
            combinationConditionList = new ArrayList<>();
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
}
