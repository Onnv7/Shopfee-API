package com.hcmute.shopfee.service.impl;

import com.hcmute.shopfee.dto.common.CouponConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.ApplicableCustomerConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.CombinationConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.MinPurchaseConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.UsageConditionDto;
import com.hcmute.shopfee.dto.request.CreateShippingCouponRequest;
import com.hcmute.shopfee.entity.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.coupon.CouponEntity;
import com.hcmute.shopfee.entity.coupon.CouponRewardEntity;
import com.hcmute.shopfee.entity.coupon.condition.ApplicableCustomerConditionEntity;
import com.hcmute.shopfee.entity.coupon.condition.CombinationConditionEntity;
import com.hcmute.shopfee.entity.coupon.condition.MinPurchaseConditionEntity;
import com.hcmute.shopfee.entity.coupon.condition.UsageConditionEntity;
import com.hcmute.shopfee.entity.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.repository.database.coupon.CouponRepository;
import com.hcmute.shopfee.service.ICouponService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService implements ICouponService {
    private final ModelMapperService modelMapperService;
    private final CouponRepository couponRepository;

    @Transactional
    private void setConditionList(List<CouponConditionDto> conditionList) {
    }

    private MinPurchaseConditionEntity getMinPurchaseConditionEntity(MinPurchaseConditionDto minPurchaseConditionDto, CouponConditionEntity minPurchase) {
        MinPurchaseConditionEntity minPurchaseConditionEntity = new MinPurchaseConditionEntity();
        minPurchaseConditionEntity.setType(minPurchaseConditionDto.getType());
        minPurchaseConditionEntity.setCouponCondition(minPurchase);

        if (minPurchaseConditionEntity.getType() == MiniPurchaseType.MONEY) {
            minPurchaseConditionEntity.setValue(minPurchaseConditionDto.getValue());
        } else if (minPurchaseConditionEntity.getType() == MiniPurchaseType.QUANTITY) {
            minPurchaseConditionEntity.setValue(minPurchaseConditionDto.getValue());
        } else if (minPurchaseConditionEntity.getType() == MiniPurchaseType.NONE) {

        }
        return minPurchaseConditionEntity;
    }

    private ApplicableCustomerConditionEntity getApplicableCustomerConditionEntity(ApplicableCustomerConditionDto applicableCustomerConditionDto, CouponConditionEntity applicableCustomer) {
        ApplicableCustomerConditionEntity applicableCustomerCondition = new ApplicableCustomerConditionEntity();
        applicableCustomerCondition.setType(applicableCustomerConditionDto.getType());
        applicableCustomerCondition.setCouponCondition(applicableCustomer);

        // TODO: check lại value từng type
        if (applicableCustomerCondition.getType() == ApplicableCustomerType.ONE) {
            applicableCustomerCondition.setValue(applicableCustomerConditionDto.getValue());
        } else if (applicableCustomerCondition.getType() == ApplicableCustomerType.ALL) {

        } else if (applicableCustomerCondition.getType() == ApplicableCustomerType.GROUP) {
            applicableCustomerCondition.setValue(applicableCustomerConditionDto.getValue());
        }
        return applicableCustomerCondition;
    }

    private List<CombinationConditionEntity> getCombinationConditionEntity(List<CombinationConditionDto> combinationConditionDtoList, CouponConditionEntity combination) {
        return combinationConditionDtoList.stream().map(condition -> {
            CombinationConditionEntity combinedConditionEntity = new CombinationConditionEntity();
            combinedConditionEntity.setType(condition.getType());
            combinedConditionEntity.setCouponCondition(combination);
            return combinedConditionEntity;
        }).toList();
    }
    private List<UsageConditionEntity> getUsageConditionEntity(List<UsageConditionDto> usageConditionDtoList, CouponConditionEntity usage) {
        return usageConditionDtoList.stream().map(condition -> {
            UsageConditionEntity usageConditionEntity = new UsageConditionEntity();
            usageConditionEntity.setType(condition.getType());
            if (usageConditionEntity.getType() == UsageConditionType.QUANTITY) {
                usageConditionEntity.setValue(condition.getValue());
            }
            usageConditionEntity.setCouponCondition(usage);
            return usageConditionEntity;
        }).toList();
    }

    @Transactional
    @Override
    public void createShippingCoupon(CreateShippingCouponRequest body) {
        CouponEntity couponEntity = modelMapperService.mapClass(body, CouponEntity.class);
        couponEntity.setCouponType(CouponType.SHIPPING);
        couponEntity.setStatus(CouponStatus.UNRELEASED);

        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.MONEY)
                .coupon(couponEntity)
                .build();

        MoneyRewardEntity moneyRewardEntity = MoneyRewardEntity.builder()
                .unit(MoneyRewardUnit.PERCENTAGE)
//                .targetReward(TargetReward.SHIPPING)
                .couponReward(couponRewardEntity)
                .value(100)
                .build();
        couponRewardEntity.setMoneyReward(moneyRewardEntity);
        couponEntity.setCouponReward(couponRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = new ArrayList<>();

        // Usage condition
        CouponConditionEntity usage = new CouponConditionEntity();
        usage.setType(ConditionType.USAGE);
        usage.setCoupon(couponEntity);

        List<UsageConditionEntity> usageConditionList = getUsageConditionEntity(body.getUsageConditionList(), usage);
        usage.setUsageConditionList(usageConditionList);
        couponConditionEntityList.add(usage);

        // Min purchase
        CouponConditionEntity minPurchase = new CouponConditionEntity();
        minPurchase.setType(ConditionType.MIN_PURCHASE);
        minPurchase.setCoupon(couponEntity);

        MinPurchaseConditionEntity minPurchaseConditionEntity = getMinPurchaseConditionEntity(body.getMinPurchaseCondition(), minPurchase);
        minPurchase.setMinPurchaseCondition(minPurchaseConditionEntity);
        couponConditionEntityList.add(minPurchase);

        // Applicable customer
        CouponConditionEntity applicableCustomer = new CouponConditionEntity();
        applicableCustomer.setType(ConditionType.CUSTOMER_TYPE);
        applicableCustomer.setCoupon(couponEntity);

        ApplicableCustomerConditionEntity applicableCustomerCondition = getApplicableCustomerConditionEntity(body.getApplicableCustomerCondition(), applicableCustomer);
        applicableCustomer.setApplicableCustomerCondition(applicableCustomerCondition);
        couponConditionEntityList.add(applicableCustomer);

        // Combination

        CouponConditionEntity combination = new CouponConditionEntity();
        combination.setType(ConditionType.COMBINATION);
        combination.setCoupon(couponEntity);

        List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
        combination.setCombinationConditionList(combinationConditionEntityList);
        couponConditionEntityList.add(combination);


        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }
}
