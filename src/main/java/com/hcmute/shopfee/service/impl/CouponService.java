package com.hcmute.shopfee.service.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.coupon.condition.*;
import com.hcmute.shopfee.dto.request.CreateBuyXGetYCouponRequest;
import com.hcmute.shopfee.dto.request.CreateOrderCouponRequest;
import com.hcmute.shopfee.dto.request.CreateProductMoneyCouponRequest;
import com.hcmute.shopfee.dto.request.CreateShippingCouponRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.entity.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.coupon.CouponEntity;
import com.hcmute.shopfee.entity.coupon.CouponRewardEntity;
import com.hcmute.shopfee.entity.coupon.condition.*;
import com.hcmute.shopfee.entity.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.entity.coupon.reward.ProductRewardEntity;
import com.hcmute.shopfee.entity.product.ProductEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.CategoryRepository;
import com.hcmute.shopfee.repository.database.coupon.CouponRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
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
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


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

//    private EligibilityCustomerConditionEntity getApplicableCustomerConditionEntity(EligibilityCustomerConditionDto eligibilityCustomerConditionDto, CouponConditionEntity applicableCustomer) {
//        EligibilityCustomerConditionEntity applicableCustomerCondition = new EligibilityCustomerConditionEntity();
//        applicableCustomerCondition.setType(eligibilityCustomerConditionDto.getType());
//        applicableCustomerCondition.setCouponCondition(applicableCustomer);
//
//        // TODO: check lại value từng type
//        if (applicableCustomerCondition.getType() == ApplicableCustomerType.ONE) {
//            applicableCustomerCondition.setValue(eligibilityCustomerConditionDto.getValue());
//        } else if (applicableCustomerCondition.getType() == ApplicableCustomerType.ALL) {
//
//        } else if (applicableCustomerCondition.getType() == ApplicableCustomerType.GROUP) {
//            applicableCustomerCondition.setValue(eligibilityCustomerConditionDto.getValue());
//        }
//        return applicableCustomerCondition;
//    }

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
            subjectConditionEntity.setType(condition.getType());
            if (subjectConditionEntity.getType() == TargetType.PRODUCT && !productRepository.existsById(condition.getValue())) {
                throw new CustomException(ErrorConstant.NOT_FOUND + condition.getValue());
            } else if (subjectConditionEntity.getType() == TargetType.CATEGORY && !categoryRepository.existsById(condition.getValue())) {
                throw new CustomException(ErrorConstant.NOT_FOUND + condition.getValue());
            }
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
//        CouponConditionEntity applicableCustomer = new CouponConditionEntity();
//        applicableCustomer.setType(ConditionType.APPLICABLE_CUSTOMER);
//        applicableCustomer.setCoupon(couponEntity);

//        EligibilityCustomerConditionEntity applicableCustomerCondition = getApplicableCustomerConditionEntity(body.getApplicableCustomerCondition(), applicableCustomer);
//        applicableCustomer.setApplicableCustomerCondition(applicableCustomerCondition);
//        couponConditionEntityList.add(applicableCustomer);

        // Combination

        CouponConditionEntity combination = new CouponConditionEntity();
        combination.setType(ConditionType.COMBINATION);
        combination.setCoupon(couponEntity);

        List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
        combination.setCombinationConditionList(combinationConditionEntityList);
        couponConditionEntityList.add(combination);

        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }


    @Override
    public void createOrderCoupon(CreateOrderCouponRequest body) {
        CouponEntity couponEntity = modelMapperService.mapClass(body, CouponEntity.class);
        couponEntity.setCouponType(CouponType.ORDER);
        couponEntity.setStatus(CouponStatus.UNRELEASED);

        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.MONEY)
                .coupon(couponEntity)
                .build();

        if (body.getUnitReward() == MoneyRewardUnit.PERCENTAGE && body.getValueReward() > 100) {
            throw new CustomException(ErrorConstant.COUPON_INVALID);
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
//        CouponConditionEntity applicableCustomer = new CouponConditionEntity();
//        applicableCustomer.setType(ConditionType.APPLICABLE_CUSTOMER);
//        applicableCustomer.setCoupon(couponEntity);
//
//        EligibilityCustomerConditionEntity applicableCustomerCondition = getApplicableCustomerConditionEntity(body.getApplicableCustomerCondition(), applicableCustomer);
//        applicableCustomer.setApplicableCustomerCondition(applicableCustomerCondition);
//        couponConditionEntityList.add(applicableCustomer);

        // Combination

        CouponConditionEntity combination = new CouponConditionEntity();
        combination.setType(ConditionType.COMBINATION);
        combination.setCoupon(couponEntity);

        List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
        combination.setCombinationConditionList(combinationConditionEntityList);
        couponConditionEntityList.add(combination);

        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }

    public void createAmountOffProductCoupon(CreateProductMoneyCouponRequest body) {
        CouponEntity couponEntity = modelMapperService.mapClass(body, CouponEntity.class);
        couponEntity.setCouponType(CouponType.PRODUCT);
        couponEntity.setStatus(CouponStatus.UNRELEASED);

        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.MONEY)
                .coupon(couponEntity)
                .build();

        if (body.getUnitReward() == MoneyRewardUnit.PERCENTAGE && body.getValueReward() > 100) {
            throw new CustomException(ErrorConstant.COUPON_INVALID);
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
//        CouponConditionEntity applicableCustomer = new CouponConditionEntity();
//        applicableCustomer.setType(ConditionType.APPLICABLE_CUSTOMER);
//        applicableCustomer.setCoupon(couponEntity);
//
//        EligibilityCustomerConditionEntity applicableCustomerCondition = getApplicableCustomerConditionEntity(body.getApplicableCustomerCondition(), applicableCustomer);
//        applicableCustomer.setApplicableCustomerCondition(applicableCustomerCondition);
//        couponConditionEntityList.add(applicableCustomer);

        // Combination

        CouponConditionEntity combination = new CouponConditionEntity();
        combination.setType(ConditionType.COMBINATION);
        combination.setCoupon(couponEntity);

        List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
        combination.setCombinationConditionList(combinationConditionEntityList);
        couponConditionEntityList.add(combination);

        // Target Object
        CouponConditionEntity targetObject = new CouponConditionEntity();
        targetObject.setType(ConditionType.TARGET_OBJECT);
        targetObject.setCoupon(couponEntity);

        List<SubjectConditionEntity> subjectConditionEntityList = getTargetObjectConditionEntity(body.getTargetObjectConditionList(), targetObject);
        targetObject.setSubjectConditionList(subjectConditionEntityList);
        couponConditionEntityList.add(targetObject);

        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }

    public void createGiftProductCoupon(CreateBuyXGetYCouponRequest body) {
        CouponEntity couponEntity = modelMapperService.mapClass(body, CouponEntity.class);
        couponEntity.setCouponType(CouponType.PRODUCT);
        couponEntity.setStatus(CouponStatus.UNRELEASED);

        // set reward
        CouponRewardEntity couponRewardEntity = CouponRewardEntity.builder()
                .type(CouponRewardType.MONEY)
                .coupon(couponEntity)
                .build();

        List<ProductRewardEntity> productRewardEntityList = new ArrayList<>();
        body.getProductRewardList().forEach(reward -> {
            ProductEntity product = productRepository.findByIdAndIsDeletedFalse(reward.getProductId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + reward.getProductId()));
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
//        CouponConditionEntity applicableCustomer = new CouponConditionEntity();
//        applicableCustomer.setType(ConditionType.APPLICABLE_CUSTOMER);
//        applicableCustomer.setCoupon(couponEntity);
//
//        EligibilityCustomerConditionEntity applicableCustomerCondition = getApplicableCustomerConditionEntity(body.getApplicableCustomerCondition(), applicableCustomer);
//        applicableCustomer.setApplicableCustomerCondition(applicableCustomerCondition);
//        couponConditionEntityList.add(applicableCustomer);

        // Combination

        CouponConditionEntity combination = new CouponConditionEntity();
        combination.setType(ConditionType.COMBINATION);
        combination.setCoupon(couponEntity);

        List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
        combination.setCombinationConditionList(combinationConditionEntityList);
        couponConditionEntityList.add(combination);

        // Target Object
        CouponConditionEntity targetObject = new CouponConditionEntity();
        targetObject.setType(ConditionType.TARGET_OBJECT);
        targetObject.setCoupon(couponEntity);

        List<SubjectConditionEntity> subjectConditionEntityList = getTargetObjectConditionEntity(body.getTargetObjectConditionList(), targetObject);
        targetObject.setSubjectConditionList(subjectConditionEntityList);
        couponConditionEntityList.add(targetObject);

        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }

    @Override
    public void deleteCoupon(String couponId) {
        CouponEntity couponCollection = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponId));
        if (couponCollection.getStatus() == CouponStatus.UNRELEASED) {
            throw new CustomException(ErrorConstant.COUPON_STATUS_UNRELEASED);
        }
        couponCollection.setDeleted(true);
        couponRepository.save(couponCollection);
    }

    @Override
    public List<GetReleaseCouponListResponse> getReleaseCouponList() {
        List<CouponEntity> couponEntityList = couponRepository.getReleaseCouponList();
        List<GetReleaseCouponListResponse> response = new ArrayList<>();
        couponEntityList.forEach(it -> {
            GetReleaseCouponListResponse coupon = GetReleaseCouponListResponse.fromCouponEntity(it);
            response.add(coupon);
        });
        return response;
    }

    @Override
    public GetReleaseCouponByIdResponse getReleaseCouponById(String couponId) {

        CouponEntity couponEntity = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponId));
        return GetReleaseCouponByIdResponse.fromCouponEntity(couponEntity);
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
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponId));
        return GetShippingCouponDetailsByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetOrderCouponDetailByIdResponse getOrderCouponDetailById(String couponId) {
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.ORDER)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponId));
        return GetOrderCouponDetailByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetProductGiftCouponDetailByIdResponse getProductGiftCouponDetailById(String couponId) {
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.ORDER)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponId));
        return GetProductGiftCouponDetailByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetAmountOffProductCouponDetailByIdResponse getAmountOffProductCouponDetailById(String couponId) {
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.ORDER)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponId));
        return GetAmountOffProductCouponDetailByIdResponse.fromCouponEntity(coupon);
    }
}
