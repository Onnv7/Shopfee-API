package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.enums.ConditionType;
import com.hcmute.shopfee.enums.CouponStatus;
import com.hcmute.shopfee.enums.CouponType;
import com.hcmute.shopfee.enums.MoneyRewardUnit;
import lombok.Data;

import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetCouponDetailsByIdResponse {
    private String id;
    private String code;
    private CouponType couponType;
    private String description;
    private ProductGift productGift;
    private MoneyDiscount moneyDiscount;
    private CouponStatus status;
    private List<Condition> conditionList;
    private Date startDate;
    private Date expirationDate;
    private Boolean canMultiple;


    @Data
    static class MoneyDiscount {
        private MoneyRewardUnit unit;
        private Long value;
    }

    @Data
    static class ProductGift {
        private String productId;
        private String size;
        private Integer quantity;
    }

    @Data
    public static class Condition {
        private String description;
        private ConditionType type;
        private Object value;
    }

}
