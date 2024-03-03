package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.dto.common.coupon.condition.*;
import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.entity.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.database.coupon.CouponRewardEntity;
import com.hcmute.shopfee.entity.database.coupon.condition.CombinationConditionEntity;
import com.hcmute.shopfee.entity.database.coupon.condition.MinPurchaseConditionEntity;
import com.hcmute.shopfee.entity.database.coupon.condition.SubjectConditionEntity;
import com.hcmute.shopfee.entity.database.coupon.condition.UsageConditionEntity;
import com.hcmute.shopfee.entity.database.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.entity.database.coupon.reward.ProductRewardEntity;
import com.hcmute.shopfee.entity.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.database.product.ProductEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.CategoryRepository;
import com.hcmute.shopfee.repository.database.coupon.CouponRepository;
import com.hcmute.shopfee.repository.database.coupon_used.CouponUsedRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.service.core.ICouponService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.SecurityUtils;
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
    private final CouponUsedRepository couponUsedRepository;


    private MinPurchaseConditionEntity getMinPurchaseConditionEntity(MinPurchaseConditionDto minPurchaseConditionDto, CouponConditionEntity minPurchase) {
        MinPurchaseConditionEntity minPurchaseConditionEntity = new MinPurchaseConditionEntity();
//        minPurchaseConditionEntity.setType(minPurchaseConditionDto.getType());
        minPurchaseConditionEntity.setCouponCondition(minPurchase);
        minPurchaseConditionEntity.setValue(minPurchaseConditionDto.getValue());

//        if (minPurchaseConditionEntity.getType() == MiniPurchaseType.MONEY) {
//            minPurchaseConditionEntity.setValue(minPurchaseConditionDto.getValue());
////        } else if (minPurchaseConditionEntity.getType() == MiniPurchaseType.QUANTITY) {
////            minPurchaseConditionEntity.setValue(minPurchaseConditionDto.getValue());
//        } else if (minPurchaseConditionEntity.getType() == MiniPurchaseType.NONE) {
//
//        }
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
//            subjectConditionEntity.setType(condition.getType());
//            if (subjectConditionEntity.getType() == TargetType.PRODUCT && !productRepository.existsById(condition.getValue())) {
//                throw new CustomException(ErrorConstant.NOT_FOUND + condition.getValue());
//            } else if (subjectConditionEntity.getType() == TargetType.CATEGORY && !categoryRepository.existsById(condition.getValue())) {
//                throw new CustomException(ErrorConstant.NOT_FOUND + condition.getValue());
//            }
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
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.PRODUCT)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponId));
        return GetProductGiftCouponDetailByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetAmountOffProductCouponDetailByIdResponse getAmountOffProductCouponDetailById(String couponId) {
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.PRODUCT)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponId));
        return GetAmountOffProductCouponDetailByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetCouponListForCartResponse getCouponListForCartResponse(GetCouponListForCartRequest body) {
        String userId = SecurityUtils.getCurrentUserId();
        List<CouponEntity> couponEntityList = couponRepository.getReleaseCouponList();
        List<CouponEntity> shippingCouponList = couponRepository.findByStatusAndCouponType(CouponStatus.RELEASED, CouponType.SHIPPING);
        List<CouponEntity> orderCouponList = couponRepository.findByStatusAndCouponType(CouponStatus.RELEASED, CouponType.ORDER);
        List<CouponEntity> productCouponList = couponRepository.findByStatusAndCouponType(CouponStatus.RELEASED, CouponType.PRODUCT);



        CouponEntity shippingCoupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(body.getShippingCouponCode(), CouponStatus.RELEASED)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getShippingCouponCode()));

        CouponEntity orderCoupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(body.getOrderCouponCode(), CouponStatus.RELEASED)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getOrderCouponCode()));

        CouponEntity productCoupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(body.getProductCouponCode(), CouponStatus.RELEASED)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getProductCouponCode()));

        checkCombinationCondition(orderCoupon, shippingCoupon, productCoupon);

        for(CouponEntity coupon : shippingCouponList) {
            List<CouponConditionEntity> conditionList = coupon.getConditionList();
            for(CouponConditionEntity condition : conditionList) {
                if(condition.getType() == ConditionType.USAGE) {
                    List<UsageConditionEntity> usageConditionList = condition.getUsageConditionList();
                    for(UsageConditionEntity usageCondition : usageConditionList) {
                        if(usageCondition.getType() == UsageConditionType.QUANTITY) {
                            if(usageCondition.getValue() == 0) {
                                // invalid
                            }
                        } else if(usageCondition.getType() == UsageConditionType.LIMIT_ONE_FOR_USER) {
                            CouponUsedEntity couponUsed = couponUsedRepository.getCouponUsedByUserIdAndCode(userId, coupon.getId()).orElse(null);
                            if(couponUsed != null) {
                                // invalid
                            }
                        }
                    }
                }
                else if(condition.getType() == ConditionType.MIN_PURCHASE) {
                    MinPurchaseConditionEntity minPurchaseCondition = condition.getMinPurchaseCondition();
                    if(body.getTotalItemPrice() < minPurchaseCondition.getValue()) {
                        // invalid
                    }
                } else if(condition.getType() == ConditionType.TARGET_OBJECT) {
                    List<SubjectConditionEntity> subjectConditionEntityList = condition.getSubjectConditionList();

                    for(SubjectConditionEntity subjectConditionEntity : subjectConditionEntityList) {
                        OrderItemDto item = body.getOrderItemList().stream().filter(it -> it.getProductId().equals(subjectConditionEntity.getObjectId())).findFirst().orElse(null);
                        if(item == null) {
                            // invalid
                        }
                    }
                }
            }
        }


        return null;
    }
    private boolean checkCombinationCondition(CouponEntity orderCoupon, CouponEntity shippingCoupon, CouponEntity productCoupon) {
        List<CouponEntity> couponEntityList = new ArrayList<>();
        List<CouponType> bag = new ArrayList<>();
        if(orderCoupon != null) {
            couponEntityList.add(orderCoupon);
            bag.add(CouponType.ORDER);
        } else if(shippingCoupon != null) {
            couponEntityList.add(shippingCoupon);
            bag.add(CouponType.SHIPPING);
        } else if(productCoupon != null) {
            couponEntityList.add(productCoupon);
            bag.add(CouponType.PRODUCT);
        }
        List<CouponConditionEntity> conditionEntityList = orderCoupon.getConditionList();
        for(CouponConditionEntity conditionEntity : conditionEntityList) {
            if(conditionEntity.getType() == ConditionType.COMBINATION) {
                List<CombinationConditionEntity> combinationConditionEntityList = conditionEntity.getCombinationConditionList();

                // danh sach coupon co the combination
                List<CouponType> listtt = new ArrayList<>();
                for(CombinationConditionEntity condition : combinationConditionEntityList) {
                    listtt.add(condition.getType());
                }
                for(CouponType item: bag) {
                    if(!listtt.contains(item)) {
                        // invalid
                    }
                }
            }
        }


        return true;
    }
}
