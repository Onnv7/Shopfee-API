package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.ItemDetailDto;
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
        couponEntity.setStatus(CouponStatus.UNRELEASED);

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
                .type(CouponRewardType.PRODUCT_GIFT)
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


        // Combination

        CouponConditionEntity combination = new CouponConditionEntity();
        combination.setType(ConditionType.COMBINATION);
        combination.setCoupon(couponEntity);

        List<CombinationConditionEntity> combinationConditionEntityList = getCombinationConditionEntity(body.getCombinationConditionList(), combination);
        combination.setCombinationConditionList(combinationConditionEntityList);
        couponConditionEntityList.add(combination);

        // Subject condition
        CouponConditionEntity subjectCondition = new CouponConditionEntity();
        subjectCondition.setType(ConditionType.TARGET_OBJECT);
        subjectCondition.setCoupon(couponEntity);

        List<SubjectConditionEntity> subjectConditionEntityList = getTargetObjectConditionEntity(body.getTargetObjectConditionList(), subjectCondition);
        subjectCondition.setSubjectConditionList(subjectConditionEntityList);
        couponConditionEntityList.add(subjectCondition);

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
        GetCouponListForCartResponse data = new GetCouponListForCartResponse();

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
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getShippingCouponCode()));
            List<CouponType> couponTypeCombinedListOfCoupon = combinationConditionRepository.getCombinationConditionByCouponCode(body.getShippingCouponCode());
            for (CouponType item : cantCombinedCouponList) {
                if (!couponTypeCombinedListOfCoupon.contains(item)) {
                    if (item == CouponType.ORDER) {
                        data.setCanCombinedWithOrderCoupon(false);
                    } else if (item == CouponType.PRODUCT) {
                        data.setCanCombinedWithProductCoupon(false);
                    }
                }
            }
        }

        if (body.getOrderCouponCode() != null) {
            couponTypeList.add(CouponType.ORDER);
            orderCoupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(body.getOrderCouponCode(), CouponStatus.RELEASED)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getOrderCouponCode()));
            List<CouponType> couponTypeCombinedListOfCoupon = combinationConditionRepository.getCombinationConditionByCouponCode(body.getOrderCouponCode());
            for (CouponType item : cantCombinedCouponList) {
                if (!couponTypeCombinedListOfCoupon.contains(item)) {
                    if (item == CouponType.SHIPPING) {
                        data.setCanCombinedWithShippingCoupon(false);
                    } else if (item == CouponType.PRODUCT) {
                        data.setCanCombinedWithProductCoupon(false);
                    }
                }
            }
        }
        if (body.getProductCouponCode() != null) {
            couponTypeList.add(CouponType.PRODUCT);
            productCoupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(body.getProductCouponCode(), CouponStatus.RELEASED)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getProductCouponCode()));
            List<CouponType> couponTypeCombinedListOfCoupon = combinationConditionRepository.getCombinationConditionByCouponCode(body.getProductCouponCode());
            for (CouponType item : cantCombinedCouponList) {
                if (!couponTypeCombinedListOfCoupon.contains(item)) {
                    if (item == CouponType.ORDER) {
                        data.setCanCombinedWithOrderCoupon(false);
                    } else if (item == CouponType.SHIPPING) {
                        data.setCanCombinedWithShippingCoupon(false);
                    }
                }
            }
        }


        List<GetCouponListForCartResponse.CouponCard> shippingCouponCard = checkCoupon(shippingCouponList, userId, body, couponTypeList, data.isCanCombinedWithShippingCoupon(), shippingCoupon);
        data.setShippingCouponList(shippingCouponCard);
        List<GetCouponListForCartResponse.CouponCard> orderCouponCard = checkCoupon(orderCouponList, userId, body, couponTypeList, data.isCanCombinedWithOrderCoupon(), orderCoupon);
        data.setOrderCouponList(orderCouponCard);
        List<GetCouponListForCartResponse.CouponCard> productCouponCard = checkCoupon(productCouponList, userId, body, couponTypeList, data.isCanCombinedWithProductCoupon(), productCoupon);
        data.setProductCouponList(productCouponCard);

        return data;
    }

    private List<GetCouponListForCartResponse.CouponCard> checkCoupon(List<CouponEntity> shippingCouponList, String userId,
                                                                      GetCouponListForCartRequest body, List<CouponType> couponTypeList, boolean canCombined,
                                                                      CouponEntity couponSelected) {
        List<GetCouponListForCartResponse.CouponCard> shippingCouponDtoList = new ArrayList<>();

        for (CouponEntity coupon : shippingCouponList) {
            GetCouponListForCartResponse.CouponCard couponCard = new GetCouponListForCartResponse.CouponCard();
            couponCard.setCouponId(coupon.getId());
            couponCard.setCode(coupon.getCode());
            couponCard.setValid(true);
            couponCard.setDescription(coupon.getDescription());
            couponCard.setExpirationDate(coupon.getExpirationDate());
            if(couponSelected != null && couponSelected.getCode().equals(coupon.getCode())) {
                couponCard.setMinPurchaseCondition(null);
                shippingCouponDtoList.add(couponCard);
                continue;
            }
            List<CouponConditionEntity> conditionList = coupon.getConditionList();

            for (CouponConditionEntity condition : conditionList) {
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
                } else if (condition.getType() == ConditionType.MIN_PURCHASE) {
                    MinPurchaseConditionEntity minPurchaseCondition = condition.getMinPurchaseCondition();
                    if (body.getTotalItemPrice() < minPurchaseCondition.getValue()) {
                        // invalid
                        couponCard.getMinPurchaseCondition().setValue(minPurchaseCondition.getValue());
                        couponCard.setValid(false);
                    } else {
                        couponCard.setMinPurchaseCondition(null);
                    }
                } else if (condition.getType() == ConditionType.TARGET_OBJECT) {
                    List<SubjectConditionEntity> subjectConditionEntityList = condition.getSubjectConditionList();

                    for (SubjectConditionEntity subjectConditionEntity : subjectConditionEntityList) {
                        OrderItemDto item = body.getOrderItemList().stream().filter(it -> it.getProductId().equals(subjectConditionEntity.getObjectId())).findFirst().orElse(null);
                        if (item == null) {
                            // invalid
                            ProductEntity productEntity = productRepository.findByIdAndIsDeletedFalse(subjectConditionEntity.getObjectId())
                                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + subjectConditionEntity.getObjectId()));
                            couponCard.getSubjectConditionList().add(new GetCouponListForCartResponse.CouponCard.SubjectCondition(productEntity.getName(), subjectConditionEntity.getValue()));
                            couponCard.setValid(false);
                        } else {
                            int count = 0;
                            for(ItemDetailDto itemDetailDto : item.getItemDetailList()) {
                                count += itemDetailDto.getQuantity();
                            }
                            if(count < subjectConditionEntity.getValue()) {
                                // invalid
                                ProductEntity productEntity = productRepository.findByIdAndIsDeletedFalse(subjectConditionEntity.getObjectId())
                                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + subjectConditionEntity.getObjectId()));
                                couponCard.getSubjectConditionList().add(new GetCouponListForCartResponse.CouponCard.SubjectCondition(productEntity.getName(), subjectConditionEntity.getValue()));
                                couponCard.setValid(false);
                            }
                        }
                    }
                } else if (condition.getType() == ConditionType.COMBINATION) {
                    if(canCombined) {
                        checkCombination(couponTypeList, condition.getCombinationConditionList(), couponCard, coupon.getCouponType());
                    } else {
                        couponCard.setValid(false);
                    }
                }

            }

            shippingCouponDtoList.add(couponCard);
        }
        return shippingCouponDtoList;
    }

    private void checkCombination(List<CouponType> couponTypeList, List<CombinationConditionEntity> conditionList, GetCouponListForCartResponse.CouponCard couponCard, CouponType couponTypeChecking) {

        List<CouponType> combinationList = new ArrayList<>();
        for (CombinationConditionEntity condition : conditionList) {
            combinationList.add(condition.getType());
        }
        // check combination xem 1 coupon trong list không thể combination với coupon nào trong cart
        for (CouponType cpType : couponTypeList) {
            if (!combinationList.contains(cpType) && couponTypeChecking != cpType) {
                // invalid
                couponCard.getCombinationConditionList().add(new GetCouponListForCartResponse.CouponCard.CombinationCondition(cpType));
                couponCard.setValid(false);
            }
        }

    }
}
