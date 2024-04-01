package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.CouponConditionDto;
import com.hcmute.shopfee.dto.common.ItemDetailDto;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.dto.common.coupon.condition.*;
import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponRewardEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.CombinationConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.MinPurchaseConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.SubjectConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.UsageConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.ProductRewardEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.CategoryRepository;
import com.hcmute.shopfee.repository.database.coupon.CouponRepository;
import com.hcmute.shopfee.repository.database.coupon.condition.CombinationConditionRepository;
import com.hcmute.shopfee.repository.database.coupon_used.CouponUsedRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.service.core.ICouponService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService implements ICouponService {
    private final ModelMapperService modelMapperService;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CouponUsedRepository couponUsedRepository;
    private final CombinationConditionRepository combinationConditionRepository;


    private MinPurchaseConditionEntity getMinPurchaseConditionEntity(MinPurchaseConditionDto minPurchaseConditionDto, CouponConditionEntity minPurchase) {
        MinPurchaseConditionEntity minPurchaseConditionEntity = new MinPurchaseConditionEntity();
        minPurchaseConditionEntity.setCouponCondition(minPurchase);
        minPurchaseConditionEntity.setValue(minPurchaseConditionDto.getValue());
        return minPurchaseConditionEntity;
    }


    private List<CombinationConditionEntity> getCombinationConditionEntity(List<CombinationConditionDto> combinationConditionDtoList, CouponConditionEntity combination) {
        return combinationConditionDtoList.stream().map(condition -> {
            CombinationConditionEntity combinedConditionEntity = new CombinationConditionEntity();
            combinedConditionEntity.setType(condition.getType());
            combinedConditionEntity.setCouponCondition(combination);
            return combinedConditionEntity;
        }).toList();
    }

    private List<SubjectConditionEntity> getTargetObjectConditionEntity(List<SubjectConditionDto> subjectConditionDtoList, CouponConditionEntity targetObject) {
        return subjectConditionDtoList.stream().map(condition -> {
            SubjectConditionEntity subjectConditionEntity = new SubjectConditionEntity();
            subjectConditionEntity.setObjectId(condition.getObjectId());
            subjectConditionEntity.setValue(condition.getValue());
            subjectConditionEntity.setCouponCondition(targetObject);
            return subjectConditionEntity;
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
        couponEntity.setStatus(CouponStatus.RELEASED);

        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.MONEY)
                .coupon(couponEntity)
                .build();

        MoneyRewardEntity moneyRewardEntity = MoneyRewardEntity.builder()
                .unit(body.getUnitReward())
                .couponReward(couponRewardEntity)
                .value(body.getValueReward())
                .build();
        couponRewardEntity.setMoneyReward(moneyRewardEntity);
        couponEntity.setCouponReward(couponRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = new ArrayList<>();

        // Usage condition
        if (body.getUsageConditionList() != null) {
            CouponConditionEntity usage = new CouponConditionEntity();
            usage.setType(ConditionType.USAGE);
            usage.setCoupon(couponEntity);

            List<UsageConditionEntity> usageConditionList = getUsageConditionEntity(body.getUsageConditionList(), usage);
            usage.setUsageConditionList(usageConditionList);
            couponConditionEntityList.add(usage);
        }

        // Min purchase
        if (body.getMinPurchaseCondition() != null) {
            CouponConditionEntity minPurchase = new CouponConditionEntity();
            minPurchase.setType(ConditionType.MIN_PURCHASE);
            minPurchase.setCoupon(couponEntity);

            MinPurchaseConditionEntity minPurchaseConditionEntity = getMinPurchaseConditionEntity(body.getMinPurchaseCondition(), minPurchase);
            minPurchase.setMinPurchaseCondition(minPurchaseConditionEntity);
            couponConditionEntityList.add(minPurchase);
        }

        // Combination
        if (body.getCombinationConditionList() != null) {
            CouponConditionEntity combination = new CouponConditionEntity();
            combination.setType(ConditionType.COMBINATION);
            combination.setCoupon(couponEntity);

            List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
            combination.setCombinationConditionList(combinationConditionEntityList);
            couponConditionEntityList.add(combination);

            // Saving coupon
            couponEntity.setConditionList(couponConditionEntityList);
            couponRepository.save(couponEntity);
        }
    }

    @Override
    public void updateShippingCoupon(UpdateShippingCouponRequest body, String couponId) {
        CouponEntity couponEntity = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_ID_NOT_FOUND + couponId));
        couponEntity.setCouponType(CouponType.SHIPPING);
        couponEntity.setStatus(body.getStatus());

        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.MONEY)
                .coupon(couponEntity)
                .build();

        MoneyRewardEntity moneyRewardEntity = MoneyRewardEntity.builder()
                .unit(body.getUnitReward())
                .couponReward(couponRewardEntity)
                .value(body.getValueReward())
                .build();
        couponRewardEntity.setMoneyReward(moneyRewardEntity);
        couponEntity.setCouponReward(couponRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = new ArrayList<>();

        // Usage condition
        if (body.getUsageConditionList() != null) {
            CouponConditionEntity usage = new CouponConditionEntity();
            usage.setType(ConditionType.USAGE);
            usage.setCoupon(couponEntity);

            List<UsageConditionEntity> usageConditionList = getUsageConditionEntity(body.getUsageConditionList(), usage);
            usage.setUsageConditionList(usageConditionList);
            couponConditionEntityList.add(usage);
        }

        // Min purchase
        if (body.getMinPurchaseCondition() != null) {
            CouponConditionEntity minPurchase = new CouponConditionEntity();
            minPurchase.setType(ConditionType.MIN_PURCHASE);
            minPurchase.setCoupon(couponEntity);

            MinPurchaseConditionEntity minPurchaseConditionEntity = getMinPurchaseConditionEntity(body.getMinPurchaseCondition(), minPurchase);
            minPurchase.setMinPurchaseCondition(minPurchaseConditionEntity);
            couponConditionEntityList.add(minPurchase);
        }

        // Combination
        if (body.getCombinationConditionList() != null) {
            CouponConditionEntity combination = new CouponConditionEntity();
            combination.setType(ConditionType.COMBINATION);
            combination.setCoupon(couponEntity);

            List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
            combination.setCombinationConditionList(combinationConditionEntityList);
            couponConditionEntityList.add(combination);

            // Saving coupon
            couponEntity.setConditionList(couponConditionEntityList);
            couponRepository.save(couponEntity);
        }
    }

    @Override
    public void createOrderCoupon(CreateOrderCouponRequest body) {
        CouponEntity couponEntity = modelMapperService.mapClass(body, CouponEntity.class);
        couponEntity.setCouponType(CouponType.ORDER);
        couponEntity.setStatus(CouponStatus.RELEASED);

        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.MONEY)
                .coupon(couponEntity)
                .build();

        if (body.getUnitReward() == MoneyRewardUnit.PERCENTAGE && body.getValueReward() > 100) {
            throw new CustomException(ErrorConstant.DATA_SEND_INVALID, "Percent cannot have a value greater than 100");
        }
        MoneyRewardEntity moneyRewardEntity = MoneyRewardEntity.builder()
                .unit(body.getUnitReward())
//                .targetReward(TargetReward.SHIPPING)
                .couponReward(couponRewardEntity)
                .value(body.getValueReward())
                .build();
        couponRewardEntity.setMoneyReward(moneyRewardEntity);
        couponEntity.setCouponReward(couponRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = new ArrayList<>();

        // Usage condition
        if (body.getUsageConditionList() != null) {
            CouponConditionEntity usage = new CouponConditionEntity();
            usage.setType(ConditionType.USAGE);
            usage.setCoupon(couponEntity);

            List<UsageConditionEntity> usageConditionList = getUsageConditionEntity(body.getUsageConditionList(), usage);
            usage.setUsageConditionList(usageConditionList);
            couponConditionEntityList.add(usage);
        }

        // Min purchase
        if (body.getMinPurchaseCondition() != null) {
            CouponConditionEntity minPurchase = new CouponConditionEntity();
            minPurchase.setType(ConditionType.MIN_PURCHASE);
            minPurchase.setCoupon(couponEntity);

            MinPurchaseConditionEntity minPurchaseConditionEntity = getMinPurchaseConditionEntity(body.getMinPurchaseCondition(), minPurchase);
            minPurchase.setMinPurchaseCondition(minPurchaseConditionEntity);
            couponConditionEntityList.add(minPurchase);
        }

        // Combination
        if (body.getCombinationConditionList() != null) {
            CouponConditionEntity combination = new CouponConditionEntity();
            combination.setType(ConditionType.COMBINATION);
            combination.setCoupon(couponEntity);

            List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
            combination.setCombinationConditionList(combinationConditionEntityList);
            couponConditionEntityList.add(combination);
        }

        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }

    @Override
    public void updateOrderCoupon(UpdateOrderCouponRequest body, String couponId) {
        CouponEntity couponEntity = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_ID_NOT_FOUND + couponId));
        couponEntity.setCouponType(CouponType.ORDER);
        couponEntity.setStatus(body.getStatus());

        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.MONEY)
                .coupon(couponEntity)
                .build();

        if (body.getUnitReward() == MoneyRewardUnit.PERCENTAGE && body.getValueReward() > 100) {
            throw new CustomException(ErrorConstant.DATA_SEND_INVALID, "Percent cannot have a value greater than 100");
        }
        MoneyRewardEntity moneyRewardEntity = MoneyRewardEntity.builder()
                .unit(body.getUnitReward())
//                .targetReward(TargetReward.SHIPPING)
                .couponReward(couponRewardEntity)
                .value(body.getValueReward())
                .build();
        couponRewardEntity.setMoneyReward(moneyRewardEntity);
        couponEntity.setCouponReward(couponRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = new ArrayList<>();

        // Usage condition
        if (body.getUsageConditionList() != null) {
            CouponConditionEntity usage = new CouponConditionEntity();
            usage.setType(ConditionType.USAGE);
            usage.setCoupon(couponEntity);

            List<UsageConditionEntity> usageConditionList = getUsageConditionEntity(body.getUsageConditionList(), usage);
            usage.setUsageConditionList(usageConditionList);
            couponConditionEntityList.add(usage);
        }

        // Min purchase
        if (body.getMinPurchaseCondition() != null) {
            CouponConditionEntity minPurchase = new CouponConditionEntity();
            minPurchase.setType(ConditionType.MIN_PURCHASE);
            minPurchase.setCoupon(couponEntity);

            MinPurchaseConditionEntity minPurchaseConditionEntity = getMinPurchaseConditionEntity(body.getMinPurchaseCondition(), minPurchase);
            minPurchase.setMinPurchaseCondition(minPurchaseConditionEntity);
            couponConditionEntityList.add(minPurchase);
        }

        // Combination
        if (body.getCombinationConditionList() != null) {
            CouponConditionEntity combination = new CouponConditionEntity();
            combination.setType(ConditionType.COMBINATION);
            combination.setCoupon(couponEntity);

            List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
            combination.setCombinationConditionList(combinationConditionEntityList);
            couponConditionEntityList.add(combination);
        }

        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }

    public void createAmountOffProductCoupon(CreateProductMoneyCouponRequest body) {
        CouponEntity couponEntity = modelMapperService.mapClass(body, CouponEntity.class);
        couponEntity.setCouponType(CouponType.PRODUCT);
        couponEntity.setStatus(CouponStatus.RELEASED);

        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.MONEY)
                .coupon(couponEntity)
                .build();

        if (body.getUnitReward() == MoneyRewardUnit.PERCENTAGE && body.getValueReward() > 100) {
            throw new CustomException(ErrorConstant.DATA_SEND_INVALID, "Percent cannot have a value greater than 100");
        }

        MoneyRewardEntity moneyRewardEntity = MoneyRewardEntity.builder()
                .unit(body.getUnitReward())
//                .targetReward(TargetReward.SHIPPING)
                .couponReward(couponRewardEntity)
                .value(body.getValueReward())
                .build();
        couponRewardEntity.setMoneyReward(moneyRewardEntity);
        couponEntity.setCouponReward(couponRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = new ArrayList<>();

        // Usage condition
        if (body.getUsageConditionList() != null) {
            CouponConditionEntity usage = new CouponConditionEntity();
            usage.setType(ConditionType.USAGE);
            usage.setCoupon(couponEntity);

            List<UsageConditionEntity> usageConditionList = getUsageConditionEntity(body.getUsageConditionList(), usage);
            usage.setUsageConditionList(usageConditionList);
            couponConditionEntityList.add(usage);
        }

        // Min purchase
        if (body.getMinPurchaseCondition() != null) {
            CouponConditionEntity minPurchase = new CouponConditionEntity();
            minPurchase.setType(ConditionType.MIN_PURCHASE);
            minPurchase.setCoupon(couponEntity);

            MinPurchaseConditionEntity minPurchaseConditionEntity = getMinPurchaseConditionEntity(body.getMinPurchaseCondition(), minPurchase);
            minPurchase.setMinPurchaseCondition(minPurchaseConditionEntity);
            couponConditionEntityList.add(minPurchase);
        }


        // Combination
        if (body.getCombinationConditionList() != null) {
            CouponConditionEntity combination = new CouponConditionEntity();
            combination.setType(ConditionType.COMBINATION);
            combination.setCoupon(couponEntity);

            List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
            combination.setCombinationConditionList(combinationConditionEntityList);
            couponConditionEntityList.add(combination);
        }


        // Target Object
        if (body.getSubjectConditionList() != null) {
            CouponConditionEntity targetObject = new CouponConditionEntity();
            targetObject.setType(ConditionType.TARGET_OBJECT);
            targetObject.setCoupon(couponEntity);

            List<SubjectConditionEntity> subjectConditionEntityList = getTargetObjectConditionEntity(body.getSubjectConditionList(), targetObject);
            targetObject.setSubjectConditionList(subjectConditionEntityList);
            couponConditionEntityList.add(targetObject);
        }


        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }

    @Override
    public void updateAmountOffProductCoupon(UpdateProductMoneyCouponRequest body, String couponId) {
        CouponEntity couponEntity = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_ID_NOT_FOUND + couponId));
        couponEntity.setCouponType(CouponType.PRODUCT);
        couponEntity.setStatus(body.getStatus());

        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.MONEY)
                .coupon(couponEntity)
                .build();

        if (body.getUnitReward() == MoneyRewardUnit.PERCENTAGE && body.getValueReward() > 100) {
            throw new CustomException(ErrorConstant.DATA_SEND_INVALID, "Percent cannot have a value greater than 100");
        }

        MoneyRewardEntity moneyRewardEntity = MoneyRewardEntity.builder()
                .unit(body.getUnitReward())
//                .targetReward(TargetReward.SHIPPING)
                .couponReward(couponRewardEntity)
                .value(body.getValueReward())
                .build();
        couponRewardEntity.setMoneyReward(moneyRewardEntity);
        couponEntity.setCouponReward(couponRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = new ArrayList<>();

        // Usage condition
        if (body.getUsageConditionList() != null) {
            CouponConditionEntity usage = new CouponConditionEntity();
            usage.setType(ConditionType.USAGE);
            usage.setCoupon(couponEntity);

            List<UsageConditionEntity> usageConditionList = getUsageConditionEntity(body.getUsageConditionList(), usage);
            usage.setUsageConditionList(usageConditionList);
            couponConditionEntityList.add(usage);
        }

        // Min purchase
        if (body.getMinPurchaseCondition() != null) {
            CouponConditionEntity minPurchase = new CouponConditionEntity();
            minPurchase.setType(ConditionType.MIN_PURCHASE);
            minPurchase.setCoupon(couponEntity);

            MinPurchaseConditionEntity minPurchaseConditionEntity = getMinPurchaseConditionEntity(body.getMinPurchaseCondition(), minPurchase);
            minPurchase.setMinPurchaseCondition(minPurchaseConditionEntity);
            couponConditionEntityList.add(minPurchase);
        }


        // Combination
        if (body.getCombinationConditionList() != null) {
            CouponConditionEntity combination = new CouponConditionEntity();
            combination.setType(ConditionType.COMBINATION);
            combination.setCoupon(couponEntity);

            List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
            combination.setCombinationConditionList(combinationConditionEntityList);
            couponConditionEntityList.add(combination);
        }


        // Target Object
        if (body.getSubjectConditionList() != null) {
            CouponConditionEntity targetObject = new CouponConditionEntity();
            targetObject.setType(ConditionType.TARGET_OBJECT);
            targetObject.setCoupon(couponEntity);

            List<SubjectConditionEntity> subjectConditionEntityList = getTargetObjectConditionEntity(body.getSubjectConditionList(), targetObject);
            targetObject.setSubjectConditionList(subjectConditionEntityList);
            couponConditionEntityList.add(targetObject);
        }


        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }

    public void createGiftProductCoupon(CreateBuyXGetYCouponRequest body) {
        CouponEntity couponEntity = modelMapperService.mapClass(body, CouponEntity.class);
        couponEntity.setCouponType(CouponType.PRODUCT);
        couponEntity.setStatus(CouponStatus.RELEASED);

        // set reward
        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.PRODUCT_GIFT)
                .coupon(couponEntity)
                .build();

        List<ProductRewardEntity> productRewardEntityList = new ArrayList<>();
        body.getProductRewardList().forEach(reward -> {
            ProductEntity product = productRepository.findById(reward.getProductId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.PRODUCT_ID_NOT_FOUND + reward.getProductId()));
            ProductRewardEntity productRewardEntity = ProductRewardEntity.builder()
                    .productId(reward.getProductId())
                    .productSize(reward.getProductSize())
                    .productName(product.getName())
                    .quantity(reward.getQuantity())
                    .couponReward(couponRewardEntity)
                    .build();
            productRewardEntityList.add(productRewardEntity);
        });
        couponRewardEntity.setProductRewardList(productRewardEntityList);

        couponEntity.setCouponReward(couponRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = new ArrayList<>();

        // Usage condition
        if (body.getUsageConditionList() != null) {
            CouponConditionEntity usage = new CouponConditionEntity();
            usage.setType(ConditionType.USAGE);
            usage.setCoupon(couponEntity);

            List<UsageConditionEntity> usageConditionList = getUsageConditionEntity(body.getUsageConditionList(), usage);
            usage.setUsageConditionList(usageConditionList);
            couponConditionEntityList.add(usage);
        }

        // Min purchase
        if (body.getMinPurchaseCondition() != null) {
            CouponConditionEntity minPurchase = new CouponConditionEntity();
            minPurchase.setType(ConditionType.MIN_PURCHASE);
            minPurchase.setCoupon(couponEntity);

            MinPurchaseConditionEntity minPurchaseConditionEntity = getMinPurchaseConditionEntity(body.getMinPurchaseCondition(), minPurchase);
            minPurchase.setMinPurchaseCondition(minPurchaseConditionEntity);
            couponConditionEntityList.add(minPurchase);
        }


        // Combination
        if (body.getCombinationConditionList() != null) {
            CouponConditionEntity combination = new CouponConditionEntity();
            combination.setType(ConditionType.COMBINATION);
            combination.setCoupon(couponEntity);

            List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
            combination.setCombinationConditionList(combinationConditionEntityList);
            couponConditionEntityList.add(combination);
        }

        // Subject condition
        if (body.getSubjectConditionList() != null) {
            CouponConditionEntity subjectCondition = new CouponConditionEntity();
            subjectCondition.setType(ConditionType.TARGET_OBJECT);
            subjectCondition.setCoupon(couponEntity);

            List<SubjectConditionEntity> subjectConditionEntityList = getTargetObjectConditionEntity(body.getSubjectConditionList(), subjectCondition);
            subjectCondition.setSubjectConditionList(subjectConditionEntityList);
            couponConditionEntityList.add(subjectCondition);
        }

        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }

    @Override
    public void updateGiftProductCoupon(UpdateBuyXGetYCouponRequest body, String couponId) {
        CouponEntity couponEntity = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_ID_NOT_FOUND + couponId));
        couponEntity.setCouponType(CouponType.PRODUCT);
        couponEntity.setStatus(body.getStatus());

        // set reward
        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.PRODUCT_GIFT)
                .coupon(couponEntity)
                .build();

        List<ProductRewardEntity> productRewardEntityList = new ArrayList<>();
        body.getProductRewardList().forEach(reward -> {
            ProductEntity product = productRepository.findById(reward.getProductId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.PRODUCT_ID_NOT_FOUND + reward.getProductId()));
            ProductRewardEntity productRewardEntity = ProductRewardEntity.builder()
                    .productId(reward.getProductId())
                    .productSize(reward.getProductSize())
                    .productName(product.getName())
                    .quantity(reward.getQuantity())
                    .couponReward(couponRewardEntity)
                    .build();
            productRewardEntityList.add(productRewardEntity);
        });
        couponRewardEntity.setProductRewardList(productRewardEntityList);

        couponEntity.setCouponReward(couponRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = new ArrayList<>();

        // Usage condition
        if (body.getUsageConditionList() != null) {
            CouponConditionEntity usage = new CouponConditionEntity();
            usage.setType(ConditionType.USAGE);
            usage.setCoupon(couponEntity);

            List<UsageConditionEntity> usageConditionList = getUsageConditionEntity(body.getUsageConditionList(), usage);
            usage.setUsageConditionList(usageConditionList);
            couponConditionEntityList.add(usage);
        }

        // Min purchase
        if (body.getMinPurchaseCondition() != null) {
            CouponConditionEntity minPurchase = new CouponConditionEntity();
            minPurchase.setType(ConditionType.MIN_PURCHASE);
            minPurchase.setCoupon(couponEntity);

            MinPurchaseConditionEntity minPurchaseConditionEntity = getMinPurchaseConditionEntity(body.getMinPurchaseCondition(), minPurchase);
            minPurchase.setMinPurchaseCondition(minPurchaseConditionEntity);
            couponConditionEntityList.add(minPurchase);
        }


        // Combination
        if (body.getCombinationConditionList() != null) {
            CouponConditionEntity combination = new CouponConditionEntity();
            combination.setType(ConditionType.COMBINATION);
            combination.setCoupon(couponEntity);

            List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
            combination.setCombinationConditionList(combinationConditionEntityList);
            couponConditionEntityList.add(combination);
        }

        // Subject condition
        if (body.getSubjectConditionList() != null) {
            CouponConditionEntity subjectCondition = new CouponConditionEntity();
            subjectCondition.setType(ConditionType.TARGET_OBJECT);
            subjectCondition.setCoupon(couponEntity);

            List<SubjectConditionEntity> subjectConditionEntityList = getTargetObjectConditionEntity(body.getSubjectConditionList(), subjectCondition);
            subjectCondition.setSubjectConditionList(subjectConditionEntityList);
            couponConditionEntityList.add(subjectCondition);
        }

        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }

    @Override
    public void deleteCoupon(String couponId) {
        CouponEntity couponCollection = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_ID_NOT_FOUND + couponId));
        if (couponCollection.getStatus() == CouponStatus.UNRELEASED) {
            throw new CustomException(ErrorConstant.ACTING_INCORRECTLY, "Actions cannot be performed on the coupon when it is in the UNRELEASED state");
        }
        couponCollection.setDeleted(true);
        couponRepository.save(couponCollection);
    }

    @Override
    public List<GetReleaseCouponListResponse> getReleaseCouponList() {
        String userId = SecurityUtils.getCurrentUserId();

        List<CouponEntity> couponEntityList = couponRepository.getReleaseCouponList();
        List<GetReleaseCouponListResponse> response = new ArrayList<>();
        for (CouponEntity couponEntity : couponEntityList) {
            GetReleaseCouponListResponse coupon = GetReleaseCouponListResponse.fromCouponEntity(couponEntity);
            response.add(coupon);

//            List<CouponConditionEntity> couponConditionEntityList = couponEntity.getConditionList();
//            for (CouponConditionEntity conditionEntity : couponConditionEntityList) {
//                ConditionType conditionType = conditionEntity.getType();
//                if (conditionType == ConditionType.USAGE) {
//                    List<UsageConditionEntity> usageConditionList = conditionEntity.getUsageConditionList();
//                    for (UsageConditionEntity usageConditionEntity : usageConditionList) {
//                        if (usageConditionEntity.getType() == UsageConditionType.QUANTITY) {
//                            int usedQuantity = couponUsedRepository.getUsedCouponCount(couponEntity.getId());
//                            if (usedQuantity > usageConditionEntity.getValue()) {
//                                // error
//                            }
//                        } else {
//                            if (couponUsedRepository.getCouponUsedByUserIdAndCode(userId, couponEntity.getCode()).isPresent()) {
//                                // error
//                            }
//                        }
//                    }
//                }
//                else if (conditionType == ConditionType.MIN_PURCHASE) {
//                    if()
//                }
//            }
        }

        return response;
    }

    @Override
    public GetReleaseCouponByIdResponse getReleaseCouponById(String couponId) {

        CouponEntity couponEntity = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_ID_NOT_FOUND + couponId));
        List<CouponConditionDto> conditionDtoData = new ArrayList<>();
        List<CouponConditionEntity> conditionEntityList = couponEntity.getConditionList();
        for (CouponConditionEntity conditionEntity : conditionEntityList) {
            CouponConditionDto conditionDto = new CouponConditionDto();
            conditionDto.setType(conditionEntity.getType());
            if (conditionEntity.getType() == ConditionType.USAGE) {
                List<UsageConditionEntity> usageConditionEntityList = conditionEntity.getUsageConditionList();
                conditionDto.setUsageConditionList(UsageConditionDto.fromUsageConditionEntityList(usageConditionEntityList));
            } else if (conditionEntity.getType() == ConditionType.TARGET_OBJECT) {
                List<SubjectConditionEntity> subjectConditionEntityList = conditionEntity.getSubjectConditionList();
                conditionDto.setSubjectConditionList(SubjectConditionDto.fromSubjectConditionEntityList(subjectConditionEntityList));
            } else if (conditionEntity.getType() == ConditionType.COMBINATION) {
                List<CombinationConditionEntity> combinationConditionEntityList = conditionEntity.getCombinationConditionList();
                conditionDto.setCombinationConditionList(CombinationConditionDto.fromCombinationConditionEntityList(combinationConditionEntityList));
            } else if (conditionEntity.getType() == ConditionType.MIN_PURCHASE) {
                MinPurchaseConditionEntity minPurchaseConditionEntity = conditionEntity.getMinPurchaseCondition();
                conditionDto.setMinPurchaseCondition(MinPurchaseConditionDto.fromMinPurchaseConditionEntity(minPurchaseConditionEntity));
            }
            conditionDtoData.add(conditionDto);
        }

        return GetReleaseCouponByIdResponse.fromCouponEntity(couponEntity, conditionDtoData);
    }

    @Override
    public List<GetCouponListResponse> getCouponList() {
        List<CouponEntity> couponEntityList = couponRepository.findByIsDeletedFalse();
        List<GetCouponListResponse> response = new ArrayList<>();
        couponEntityList.forEach(it -> {
            GetCouponListResponse coupon = GetCouponListResponse.fromCouponEntity(it);
            response.add(coupon);
        });
        return response;
    }

    @Override
    public GetShippingCouponDetailsByIdResponse getShippingCouponDetailById(String couponId) {
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.SHIPPING)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_ID_NOT_FOUND + couponId));
        return GetShippingCouponDetailsByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetOrderCouponDetailByIdResponse getOrderCouponDetailById(String couponId) {
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.ORDER)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_ID_NOT_FOUND + couponId));
        return GetOrderCouponDetailByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetProductGiftCouponDetailByIdResponse getProductGiftCouponDetailById(String couponId) {
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.PRODUCT)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_ID_NOT_FOUND + couponId));
        return GetProductGiftCouponDetailByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetAmountOffProductCouponDetailByIdResponse getAmountOffProductCouponDetailById(String couponId) {
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.PRODUCT)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_ID_NOT_FOUND + couponId));
        return GetAmountOffProductCouponDetailByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetCouponListForCartResponse getCouponListForCartResponse(GetCouponListForCartRequest body) {
        String userId = SecurityUtils.getCurrentUserId();
        GetCouponListForCartResponse data = new GetCouponListForCartResponse();

        data.setCanCombinedWithProductCoupon(true);
        data.setCanCombinedWithOrderCoupon(true);
        data.setCanCombinedWithShippingCoupon(true);

        List<CouponEntity> shippingCouponList = couponRepository.findByStatusAndCouponType(CouponStatus.RELEASED, CouponType.SHIPPING);
        List<CouponEntity> orderCouponList = couponRepository.findByStatusAndCouponType(CouponStatus.RELEASED, CouponType.ORDER);
        List<CouponEntity> productCouponList = couponRepository.findByStatusAndCouponType(CouponStatus.RELEASED, CouponType.PRODUCT);

        CouponEntity shippingCoupon = null;
        CouponEntity orderCoupon = null;
        CouponEntity productCoupon = null;

        List<CouponType> couponTypeList = new ArrayList<>();
        List<CouponType> cantCombinedCouponList = Arrays.asList(CouponType.PRODUCT, CouponType.ORDER, CouponType.SHIPPING);
        if (body.getShippingCouponCode() != null) {
            couponTypeList.add(CouponType.SHIPPING);
            shippingCoupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(body.getShippingCouponCode(), CouponStatus.RELEASED)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_CODE_NOT_FOUND + body.getShippingCouponCode()));
            List<CouponType> couponTypeCombinedListOfCoupon = combinationConditionRepository.getCombinationConditionByCouponCode(body.getShippingCouponCode());
            if (!couponTypeCombinedListOfCoupon.contains(CouponType.ORDER)) {
                data.setCanCombinedWithOrderCoupon(false);
            } else if (!couponTypeCombinedListOfCoupon.contains(CouponType.PRODUCT)) {
                data.setCanCombinedWithProductCoupon(false);
            }
        }

        if (body.getOrderCouponCode() != null) {
            couponTypeList.add(CouponType.ORDER);
            orderCoupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(body.getOrderCouponCode(), CouponStatus.RELEASED)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_CODE_NOT_FOUND + body.getOrderCouponCode()));
            List<CouponType> couponTypeCombinedListOfCoupon = combinationConditionRepository.getCombinationConditionByCouponCode(body.getOrderCouponCode());
            if (!couponTypeCombinedListOfCoupon.contains(CouponType.SHIPPING)) {
                data.setCanCombinedWithShippingCoupon(false);
            } else if (!couponTypeCombinedListOfCoupon.contains(CouponType.PRODUCT)) {
                data.setCanCombinedWithProductCoupon(false);
            }
        }
        if (body.getProductCouponCode() != null) {
            couponTypeList.add(CouponType.PRODUCT);
            productCoupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(body.getProductCouponCode(), CouponStatus.RELEASED)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_CODE_NOT_FOUND + body.getProductCouponCode()));
            List<CouponType> couponTypeCombinedListOfCoupon = combinationConditionRepository.getCombinationConditionByCouponCode(body.getProductCouponCode());
            if (!couponTypeCombinedListOfCoupon.contains(CouponType.SHIPPING)) {
                data.setCanCombinedWithShippingCoupon(false);
            } else if (!couponTypeCombinedListOfCoupon.contains(CouponType.ORDER)) {
                data.setCanCombinedWithOrderCoupon(false);
            }
        }


        List<GetCouponListForCartResponse.CouponCard> shippingCouponCard = checkCouponListForType(shippingCouponList, userId, body, couponTypeList, data.isCanCombinedWithShippingCoupon(), shippingCoupon);
        data.setShippingCouponList(shippingCouponCard);
        List<GetCouponListForCartResponse.CouponCard> orderCouponCard = checkCouponListForType(orderCouponList, userId, body, couponTypeList, data.isCanCombinedWithOrderCoupon(), orderCoupon);
        data.setOrderCouponList(orderCouponCard);
        List<GetCouponListForCartResponse.CouponCard> productCouponCard = checkCouponListForType(productCouponList, userId, body, couponTypeList, data.isCanCombinedWithProductCoupon(), productCoupon);
        data.setProductCouponList(productCouponCard);

        return data;
    }

    @Override
    public List<CheckCouponInCartResponse> checkingCouponListInCart(GetCouponListForCartRequest body) {
        List<CheckCouponInCartResponse> data = new ArrayList<>();
        String userId = SecurityUtils.getCurrentUserId();
        List<OrderItemDto> orderItemDtoList = body.getOrderItemList();
        if (body.getOrderCouponCode() != null) {
            data.add(checkCouponWithCodeAndOrderItemCart(body.getOrderCouponCode(), body.getTotalPayment(), userId, orderItemDtoList));
        }
        if (body.getProductCouponCode() != null) {
            data.add(checkCouponWithCodeAndOrderItemCart(body.getProductCouponCode(), body.getTotalPayment(), userId, orderItemDtoList));
        }
        if (body.getShippingCouponCode() != null) {
            data.add(checkCouponWithCodeAndOrderItemCart(body.getShippingCouponCode(), body.getTotalPayment(), userId, orderItemDtoList));
        }
        return data;
    }

    private CheckCouponInCartResponse checkCouponWithCodeAndOrderItemCart(String couponCode, long totalPayment, String userId, List<OrderItemDto> orderItemDtoList) {
        CheckCouponInCartResponse couponResult = new CheckCouponInCartResponse();
        CouponEntity couponEntity = couponRepository.findByCodeAndStatusAndIsDeletedFalse(couponCode, CouponStatus.RELEASED)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_CODE_NOT_FOUND + couponCode));
        couponResult.setCouponType(couponEntity.getCouponType());

        CheckCouponInCartResponse.ViolatedCondition violatedCondition = new CheckCouponInCartResponse.ViolatedCondition();

        List<CouponConditionEntity> couponConditionEntityList = couponEntity.getConditionList();
        List<String> productIdListInCart = new ArrayList<>();
        List<String> subjectIdListInCart = new ArrayList<>();

        for (CouponConditionEntity conditionEntity : couponConditionEntityList) {
            ConditionType conditionType = conditionEntity.getType();
            if (conditionType == ConditionType.MIN_PURCHASE) {
                long minPurchaseValue = conditionEntity.getMinPurchaseCondition().getValue();
                if (totalPayment < minPurchaseValue) {
                    // invalid
                    couponResult.setValid(false);
                    CheckCouponInCartResponse.MinPurchaseCondition minPurchaseCondition = new CheckCouponInCartResponse.MinPurchaseCondition(minPurchaseValue);
                    violatedCondition.setMinPurchaseCondition(minPurchaseCondition);
                }
            } else if (conditionType == ConditionType.USAGE) {
                List<UsageConditionEntity> usageConditionEntityList = conditionEntity.getUsageConditionList();
                List<CheckCouponInCartResponse.UsageCondition> usageConditionList = new ArrayList<>();
                for (UsageConditionEntity usageCondition : usageConditionEntityList) {
                    if (usageCondition.getType() == UsageConditionType.QUANTITY) {
                        long usedCount = couponUsedRepository.getUsedCouponCount(couponEntity.getId());
                        if (usedCount >= usageCondition.getValue()) {
                            // invalid
                            couponResult.setValid(false);
                            usageConditionList.add(new CheckCouponInCartResponse.UsageCondition(UsageConditionType.QUANTITY, usageCondition.getValue()));
                        }
                    } else if (usageCondition.getType() == UsageConditionType.LIMIT_ONE_FOR_USER) {
                        CouponUsedEntity couponUsed = couponUsedRepository.getCouponUsedByUserIdAndCode(userId, couponEntity.getId()).orElse(null);
                        if (couponUsed != null) {
                            // invalid
                            couponResult.setValid(false);
                            usageConditionList.add(new CheckCouponInCartResponse.UsageCondition(UsageConditionType.LIMIT_ONE_FOR_USER, null));
                        }
                    }
                }
                violatedCondition.setUsageConditionList(usageConditionList);
            }
            else if (conditionType == ConditionType.TARGET_OBJECT) {
                List<SubjectConditionEntity> subjectConditionEntityList = conditionEntity.getSubjectConditionList();

                List<CheckCouponInCartResponse.SubjectCondition> subjectConditionList = new ArrayList<>();
                productIdListInCart = orderItemDtoList.stream().map(OrderItemDto::getProductId).toList();
                subjectIdListInCart = subjectConditionEntityList.stream().map(SubjectConditionEntity::getObjectId).toList();
                for (SubjectConditionEntity subjectConditionEntity : subjectConditionEntityList) {
                    OrderItemDto item = orderItemDtoList.stream()
                            .filter(it -> it.getProductId().equals(subjectConditionEntity.getObjectId()))
                            .findFirst().orElse(null);
                    ProductEntity productEntity = productRepository.findById(subjectConditionEntity.getObjectId())
                            .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.PRODUCT_ID_NOT_FOUND + subjectConditionEntity.getObjectId()));

                    // trong cart khong co product item thoa man
                    if (item == null) {
                        // invalid

                        couponResult.setValid(false);
                        subjectConditionList.add(new CheckCouponInCartResponse.SubjectCondition(productEntity.getName(), subjectConditionEntity.getValue()));
                    }
                    // trong cart co product item thoa man
                    else {
                        int count = 0;
                        for (ItemDetailDto itemDetailDto : item.getItemDetailList()) {
                            count += itemDetailDto.getQuantity();
                        }
                        if (count < subjectConditionEntity.getValue()) {
                            // invalid
                            couponResult.setValid(false);
                            subjectConditionList.add(new CheckCouponInCartResponse.SubjectCondition(productEntity.getName(), subjectConditionEntity.getValue()));
                        }
                    }
                }
                violatedCondition.setSubjectConditionList(subjectConditionList);
            }
        }
        if (couponResult.isValid()) {
            CouponRewardEntity couponRewardEntity = couponEntity.getCouponReward();
            CheckCouponInCartResponse.Reward reward = new CheckCouponInCartResponse.Reward();

            if (couponRewardEntity.getType() == CouponRewardType.MONEY) {
                reward.setMoneyReward(CheckCouponInCartResponse.fromMoneyRewardEntity(couponRewardEntity.getMoneyReward()));
            } else if (couponRewardEntity.getType() == CouponRewardType.PRODUCT_GIFT) {
                reward.setProductRewardList(CheckCouponInCartResponse.fromProductRewardEntityList(couponRewardEntity.getProductRewardList()));
            }
            if(couponEntity.getCouponType() == CouponType.PRODUCT) {
                for(String subjectId: subjectIdListInCart) {
                    if(productIdListInCart.contains(subjectId)) {
                        ProductEntity productEntity = productRepository.findById(subjectId)
                                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.PRODUCT_ID_NOT_FOUND + subjectId));

                        CheckCouponInCartResponse.SubjectInformation subjectInformation = new CheckCouponInCartResponse.SubjectInformation(subjectId, productEntity.getName());
                        reward.setSubjectInformation(subjectInformation);
                    }
                }
            }
            couponResult.setReward(reward);
        } else {
            couponResult.setViolatedCondition(violatedCondition);
        }
        return couponResult;
    }

//    @Override
//    public GetRewardOfCouponResponse getRewardOfCoupon(String couponId) {
//        CouponEntity couponEntity = couponRepository.findByIdAndIsDeletedFalse(couponId)
//                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.COUPON_ID_NOT_FOUND + couponId));
//        CouponRewardEntity couponRewardEntity = couponEntity.getCouponReward();
//
//        GetRewardOfCouponResponse data = new GetRewardOfCouponResponse();
//
//        if (couponEntity.getCouponType() == CouponType.PRODUCT) {
//            CouponConditionEntity conditionEntity = couponEntity.getConditionList().stream().filter(condition -> condition.getType() == ConditionType.TARGET_OBJECT).findFirst().orElseThrow(
//                    () -> new CustomException(ErrorConstant.SERVER_ERROR, "Coupon is not valid")
//            );
////            ProductEntity productEntity = productRepository.findById()
////                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.PRODUCT_ID_NOT_FOUND + subjectConditionEntity.getObjectId()));
//
//        }
//        if (couponRewardEntity.getType() == CouponRewardType.PRODUCT_GIFT) {
//            List<ProductRewardEntity> productRewardEntityList = couponRewardEntity.getProductRewardList();
//            data.setProductRewardList(GetRewardOfCouponResponse.fromProductRewardEntityList(productRewardEntityList));
//        } else if (couponRewardEntity.getType() == CouponRewardType.MONEY) {
//            data.setMoneyReward(GetRewardOfCouponResponse.fromMoneyRewardEntity(couponRewardEntity.getMoneyReward()));
//        }
//        return data;
//    }

    private List<GetCouponListForCartResponse.CouponCard> checkCouponListForType(List<CouponEntity> couponEntityList, String userId,
                                                                                 GetCouponListForCartRequest body, List<CouponType> couponTypeListInCart, boolean canCombined,
                                                                                 CouponEntity couponSelected) {
        List<GetCouponListForCartResponse.CouponCard> couponCardList = new ArrayList<>();

        for (CouponEntity coupon : couponEntityList) {
            GetCouponListForCartResponse.CouponCard couponCard = new GetCouponListForCartResponse.CouponCard();
            couponCard.setCouponId(coupon.getId());
            couponCard.setCode(coupon.getCode());
            couponCard.setValid(true);
            couponCard.setDescription(coupon.getDescription());
            couponCard.setExpirationDate(coupon.getExpirationDate());

            // neu la coupon dang duoc chon => bo qua
            if (couponSelected != null && couponSelected.getCode().equals(coupon.getCode())) {
//                couponCard.setMinPurchaseCondition(null);
                couponCardList.add(couponCard);
                continue;
            }
            List<CouponConditionEntity> conditionList = coupon.getConditionList();

            for (CouponConditionEntity condition : conditionList) {
                // check USAGE
                if (condition.getType() == ConditionType.USAGE) {
                    List<UsageConditionEntity> usageConditionList = condition.getUsageConditionList();
                    for (UsageConditionEntity usageCondition : usageConditionList) {
                        if (usageCondition.getType() == UsageConditionType.QUANTITY) {
                            int usedCount = couponUsedRepository.getUsedCouponCount(coupon.getId());
                            if (usedCount >= usageCondition.getValue()) {
                                // invalid
                                couponCard.getUsageConditionList().add(new GetCouponListForCartResponse.CouponCard.UsageCondition(usageCondition.getType(), 0));
                                couponCard.setValid(false);
                            }
                        } else if (usageCondition.getType() == UsageConditionType.LIMIT_ONE_FOR_USER) {
                            CouponUsedEntity couponUsed = couponUsedRepository.getCouponUsedByUserIdAndCode(userId, coupon.getId()).orElse(null);
                            if (couponUsed != null) {
                                couponCard.getUsageConditionList().add(new GetCouponListForCartResponse.CouponCard.UsageCondition(usageCondition.getType(), null));
                                couponCard.setValid(false);
                            }
                        }
                    }
                }
                // check MIN_PURCHASE
                else if (condition.getType() == ConditionType.MIN_PURCHASE) {
                    MinPurchaseConditionEntity minPurchaseCondition = condition.getMinPurchaseCondition();
                    if (body.getTotalItemPrice() < minPurchaseCondition.getValue()) {
                        // invalid
                        couponCard.getMinPurchaseCondition().setValue(minPurchaseCondition.getValue());
                        couponCard.setValid(false);
                    } else {
                        couponCard.setMinPurchaseCondition(null);
                    }
                }
                // check TARGET_OBJECT
                else if (condition.getType() == ConditionType.TARGET_OBJECT) {
                    List<SubjectConditionEntity> subjectConditionEntityList = condition.getSubjectConditionList();

                    for (SubjectConditionEntity subjectConditionEntity : subjectConditionEntityList) {
                        OrderItemDto item = body.getOrderItemList().stream().filter(it -> it.getProductId().equals(subjectConditionEntity.getObjectId())).findFirst().orElse(null);
                        if (item == null) {
                            // invalid
                            ProductEntity productEntity = productRepository.findById(subjectConditionEntity.getObjectId())
                                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.PRODUCT_ID_NOT_FOUND + subjectConditionEntity.getObjectId()));
                            couponCard.getSubjectConditionList().add(new GetCouponListForCartResponse.CouponCard.SubjectCondition(productEntity.getName(), subjectConditionEntity.getValue()));
                            couponCard.setValid(false);
                        } else {
                            int count = 0;
                            for (ItemDetailDto itemDetailDto : item.getItemDetailList()) {
                                count += itemDetailDto.getQuantity();
                            }
                            if (count < subjectConditionEntity.getValue()) {
                                // invalid
                                ProductEntity productEntity = productRepository.findById(subjectConditionEntity.getObjectId())
                                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.PRODUCT_ID_NOT_FOUND + subjectConditionEntity.getObjectId()));
                                couponCard.getSubjectConditionList().add(new GetCouponListForCartResponse.CouponCard.SubjectCondition(productEntity.getName(), subjectConditionEntity.getValue()));
                                couponCard.setValid(false);
                            }
                        }
                    }
                } else if (condition.getType() == ConditionType.COMBINATION) {
                    if (canCombined) {
                        checkCombination(couponTypeListInCart, condition.getCombinationConditionList(), couponCard, coupon.getCouponType());
                    } else {
                        couponCard.setValid(false);
                    }
                }

            }

            couponCardList.add(couponCard);
        }
        return couponCardList;
    }

    // kiem tra chieu nguoc lai, coupon o list co the combine voi coupon trong cart khong
    private void checkCombination(List<CouponType> couponTypeListInCart, List<CombinationConditionEntity> conditionList, GetCouponListForCartResponse.CouponCard couponCard, CouponType couponTypeChecking) {

        List<CouponType> combinationList = new ArrayList<>();
        for (CombinationConditionEntity condition : conditionList) {
            combinationList.add(condition.getType());
        }
        // check combination xem 1 coupon trong list không thể combination với coupon nào trong cart
        for (CouponType couponType : couponTypeListInCart) {
            // neu cac coupon list khong chua loai couponType trong cart => khong the combine tu list -> cart
            if (!combinationList.contains(couponType) && couponTypeChecking != couponType) {
                // invalid
                couponCard.getCombinationConditionList().add(new GetCouponListForCartResponse.CouponCard.CombinationCondition(couponType));
                couponCard.setValid(false);
            }
        }

    }
}
