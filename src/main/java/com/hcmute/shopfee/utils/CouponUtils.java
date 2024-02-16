package com.hcmute.shopfee.utils;

import com.hcmute.shopfee.entity.coupon.condition.UsageConditionEntity;
import com.hcmute.shopfee.entity.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.enums.UsageConditionType;
import com.hcmute.shopfee.repository.database.coupon_used.CouponUsedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponUtils {
    @Autowired
    private static CouponUsedRepository couponUsedRepository;
    public static boolean checkUsageCondition(String userId, String couponCode, List<UsageConditionEntity> usageConditionList) {
        for (UsageConditionEntity usageCondition : usageConditionList) {
            UsageConditionType type = usageCondition.getType();
            switch (type) {
                case QUANTITY -> {
                    if (usageCondition.getValue() <= 0) {
                        return false;
                    }
                }
                case LIMIT_ONE_FOR_USER -> {
                    List<CouponUsedEntity> couponUsedEntityList = couponUsedRepository.getCouponUsedByUserIdAndCode(userId, couponCode);
                    if (!couponUsedEntityList.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
