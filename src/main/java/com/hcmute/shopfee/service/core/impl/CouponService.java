package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.CouponConditionDto;
import com.hcmute.shopfee.dto.common.ItemDetailDto;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.dto.common.coupon.condition.*;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.CombinationConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.MinPurchaseConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.SubjectConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.UsageConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.ProductRewardEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.payload.request.*;
import com.hcmute.shopfee.payload.response.*;
import com.hcmute.shopfee.repository.database.CategoryRepository;
import com.hcmute.shopfee.repository.database.coupon.CouponConditionRepository;
import com.hcmute.shopfee.repository.database.coupon.CouponRepository;
import com.hcmute.shopfee.repository.database.coupon.condition.CombinationConditionRepository;
import com.hcmute.shopfee.repository.database.coupon.condition.SubjectConditionRepository;
import com.hcmute.shopfee.repository.database.coupon.condition.UsageConditionRepository;
import com.hcmute.shopfee.repository.database.coupon.reward.MoneyRewardRepository;
import com.hcmute.shopfee.repository.database.coupon.reward.ProductRewardRepository;
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
    private final MoneyRewardRepository moneyRewardRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CouponUsedRepository couponUsedRepository;
    private final CombinationConditionRepository combinationConditionRepository;
    private final UsageConditionRepository usageConditionRepository;
    private final CouponConditionRepository couponConditionRepository;
    private final ProductRewardRepository productRewardRepository;
    private final SubjectConditionRepository subjectConditionRepository;

    private boolean isExistedCouponCode(String couponCode) {
        return couponRepository.findByCodeAndIsDeletedFalse(couponCode).orElse(null) != null;
    }
    private List<CouponConditionEntity> getCouponConditionList(
            List<UsageConditionDto> usageConditionDtoList, MinPurchaseConditionDto minPurchaseConditionDto,
            List<CombinationConditionDto> combinationConditionDtoList, List<SubjectConditionDto> subjectConditionDtoList, CouponEntity couponEntity) {
        List<CouponConditionEntity> couponConditionEntityList = new ArrayList<>();
        // Usage condition
        if (usageConditionDtoList != null) {
            CouponConditionEntity couponConditionEntity = new CouponConditionEntity();
            couponConditionEntity.setType(ConditionType.USAGE);
            couponConditionEntity.setCoupon(couponEntity);

            if (usageConditionDtoList.size() > 2 || (usageConditionDtoList.size() == 2 && usageConditionDtoList.get(0).getType() == usageConditionDtoList.get(1).getType())) {
                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Usage condition is invalid");
            }

            List<UsageConditionEntity> usageConditionList = usageConditionDtoList.stream()
                    .map(condition -> {
                        UsageConditionEntity usageConditionEntity = new UsageConditionEntity();
                        usageConditionEntity.setType(condition.getType());
                        if (usageConditionEntity.getType() == UsageConditionType.QUANTITY) {
                            if (condition.getValue() == null || condition.getValue() <= 0) {
                                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, ErrorConstant.NEED_TO_SEND_QUANTITY_VALUE_FOR_USAGE_CONDITION);
                            }
                            usageConditionEntity.setValue(condition.getValue());
                        }
                        usageConditionEntity.setCouponCondition(couponConditionEntity);
                        return usageConditionEntity;
                    }).toList();
            couponConditionEntity.setUsageConditionList(usageConditionList);
            couponConditionEntityList.add(couponConditionEntity);
        }

        // Min purchase
        if (minPurchaseConditionDto != null) {
            CouponConditionEntity couponConditionEntity = CouponConditionEntity.builder()
                    .type(ConditionType.MIN_PURCHASE)
                    .coupon(couponEntity)
                    .build();

            MinPurchaseConditionEntity minPurchaseConditionEntity = MinPurchaseConditionEntity.builder()
                    .couponCondition(couponConditionEntity)
                    .value(minPurchaseConditionDto.getValue())
                    .build();

            couponConditionEntity.setMinPurchaseCondition(minPurchaseConditionEntity);
            couponConditionEntityList.add(couponConditionEntity);
        }


        // Combination
        if (combinationConditionDtoList != null) {
            CouponConditionEntity couponConditionEntity = new CouponConditionEntity();
            couponConditionEntity.setType(ConditionType.COMBINATION);
            couponConditionEntity.setCoupon(couponEntity);

            List<CombinationConditionEntity> combinationConditionEntityList = combinationConditionDtoList.stream()
                    .map(condition -> {
                        CombinationConditionEntity combinedConditionEntity = new CombinationConditionEntity();
                        combinedConditionEntity.setType(condition.getType());
                        combinedConditionEntity.setCouponCondition(couponConditionEntity);
                        return combinedConditionEntity;
                    }).toList();
            couponConditionEntity.setCombinationConditionList(combinationConditionEntityList);
            couponConditionEntityList.add(couponConditionEntity);
        }

        // Subject condition
        if (subjectConditionDtoList != null) {
            CouponConditionEntity couponConditionEntity = new CouponConditionEntity();
            couponConditionEntity.setType(ConditionType.SUBJECT);
            couponConditionEntity.setCoupon(couponEntity);

            List<SubjectConditionEntity> subjectConditionEntityList = subjectConditionDtoList.stream()
                    .map(condition -> {
                        ProductEntity productEntity = productRepository.findById(condition.getObjectId())
                                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + condition.getObjectId()));
                        SubjectConditionEntity subjectConditionEntity = new SubjectConditionEntity();
                        subjectConditionEntity.setObjectId(condition.getObjectId());
                        subjectConditionEntity.setValue(condition.getValue());
                        subjectConditionEntity.setProductName(productEntity.getName());
                        subjectConditionEntity.setCouponCondition(couponConditionEntity);
                        return subjectConditionEntity;
                    }).toList();
            couponConditionEntity.setSubjectConditionList(subjectConditionEntityList);
            couponConditionEntityList.add(couponConditionEntity);
        }
        return couponConditionEntityList;
    }

    private static MoneyRewardEntity setMoneyReward( MoneyRewardUnit unit, Integer value, CouponEntity couponEntity) {
        if (unit == MoneyRewardUnit.PERCENTAGE && value > 100) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, ErrorConstant.CANNOT_HAVE_A_VALUE_GREATER_THAN_100);
        }
//        moneyRewardEntity.setValue(value);
//        moneyRewardEntity.setUnit(unit);
        return new MoneyRewardEntity(unit, value, couponEntity);
//        return MoneyRewardEntity.builder()
//                .unit(unit)
//                .coupon(couponEntity)
//                .value(value)
//                .build();
    }


    @Transactional
    @Override
    public void createShippingCoupon(CreateShippingCouponRequest body) {
        if(isExistedCouponCode(body.getCode())) {
            throw new ShopfeeException(ShopfeeErrorCode.COUPON_CODE_EXISTED);
        }
        CouponEntity couponEntity = modelMapperService.mapClass(body, CouponEntity.class);
        couponEntity.setCouponType(CouponType.SHIPPING);
        couponEntity.setStatus(CouponStatus.RELEASED);
        couponEntity.setRewardType(CouponRewardType.MONEY);

        MoneyRewardEntity moneyRewardEntity = setMoneyReward(body.getUnitReward(), body.getValueReward(), couponEntity);
//        couponEntity.setUnit(body.getUnitReward());
//        couponEntity.setValue(body.getValueReward());
        couponEntity.setMoneyReward(moneyRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = getCouponConditionList(body.getUsageConditionList(), body.getMinPurchaseCondition(), body.getCombinationConditionList(), null, couponEntity);

        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        couponRepository.save(couponEntity);
    }

    @Transactional
    @Override
    public void updateShippingCoupon(UpdateShippingCouponRequest body, String couponId) {
        CouponEntity couponEntity =couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + couponId));

        couponEntity.setCouponType(CouponType.SHIPPING);
        couponEntity.setStatus(body.getStatus());
        couponEntity.setRewardType(CouponRewardType.MONEY);
        couponEntity.setCode(body.getCode());
        couponEntity.setDescription(body.getDescription());
        couponEntity.setStartDate(body.getStartDate());
        couponEntity.setExpirationDate(body.getExpirationDate());


        couponEntity.getMoneyReward().setUnit(body.getUnitReward());
        couponEntity.getMoneyReward().setValue(body.getValueReward());



        // Usage condition
        List<UsageConditionDto> usageConditionDtoList = body.getUsageConditionList();
        modifyUsageCondition(usageConditionDtoList, couponEntity);


        // Min purchase
        MinPurchaseConditionDto minPurchaseConditionDto = body.getMinPurchaseCondition();
        modifyMinPurchaseCondition(couponEntity, minPurchaseConditionDto);

        // Combination
        List<CombinationConditionDto> combinationConditionDtoList = body.getCombinationConditionList();
        modifyCombinationCondition(combinationConditionDtoList, couponEntity);


        // Saving coupon
        couponRepository.save(couponEntity);
    }

    private void modifyMinPurchaseCondition(CouponEntity couponEntity, MinPurchaseConditionDto minPurchaseConditionDto) {
        List<CouponConditionEntity> conditionEntityList = couponEntity.getConditionList();
        CouponConditionEntity condition = conditionEntityList.stream().filter(cd -> cd.getType() == ConditionType.MIN_PURCHASE).findFirst().orElseThrow(
                () -> new ShopfeeException(ShopfeeErrorCode.CATEGORY_NOT_FOUND, ErrorConstant.ERROR_DUE_TO_MISSING_DATA_DURING_CREATION_PROCESS)
        );
        MinPurchaseConditionEntity minPurchaseConditionEntity = condition.getMinPurchaseCondition();
        minPurchaseConditionEntity.setValue(minPurchaseConditionDto.getValue());
    }

    private void modifyCombinationCondition(List<CombinationConditionDto> combinationConditionDtoList, CouponEntity couponEntity) {
        List<CouponConditionEntity> conditionEntityList = couponEntity.getConditionList();
        CouponConditionEntity condition = conditionEntityList.stream().filter(cd -> cd.getType() == ConditionType.COMBINATION).findFirst().orElse(null);
        if (condition == null) {
            if (combinationConditionDtoList == null || combinationConditionDtoList.isEmpty()) {
                return;
            }
        }

        List<CombinationConditionEntity> combinationConditionEntityList = condition != null ? condition.getCombinationConditionList() : new ArrayList<>();
        List<CombinationConditionEntity> newCombinationList = new ArrayList<>();

        for (CombinationConditionDto combinationConditionDto : combinationConditionDtoList) {
            boolean needInsert = true;
            for (CombinationConditionEntity combinationConditionEntity : combinationConditionEntityList) {
                if (combinationConditionEntity.getType() == combinationConditionDto.getType()) {
                    needInsert = false;
                }
            }
            if (needInsert) {
                CombinationConditionEntity newCombination = CombinationConditionEntity.builder()
                        .couponCondition(condition)
                        .type(combinationConditionDto.getType())
                        .build();
                newCombinationList.add(newCombination);
            }
        }
        combinationConditionEntityList.addAll(newCombinationList);

        List<CombinationConditionEntity> deletedUsageList = new ArrayList<>();
        for (CombinationConditionEntity combinationConditionEntity : combinationConditionEntityList) {
            boolean needDelete = true;
            for (CombinationConditionDto combinationConditionDto : combinationConditionDtoList) {
                if (combinationConditionEntity.getType() == combinationConditionDto.getType()) {
                    // tìm thấy -> khoong can delete
                    needDelete = false;
                    break;
                }
            }
            // neu khong tim thay trong user gui len -> xoa
            if (needDelete) {
                deletedUsageList.add(combinationConditionEntity);
                combinationConditionRepository.delete(combinationConditionEntity);
            }
        }
        combinationConditionEntityList.removeAll(deletedUsageList);

        // neu cac con bi xoa het -> xoa luon usage condition
        if (combinationConditionEntityList.isEmpty() && condition != null) {
            conditionEntityList.remove(condition);
            couponConditionRepository.delete(condition);
        }

        if (condition == null) {
            CouponConditionEntity conditionEntity = CouponConditionEntity.builder()
                    .type(ConditionType.COMBINATION)
                    .combinationConditionList(combinationConditionEntityList)
                    .coupon(couponEntity)
                    .build();
            for (CombinationConditionEntity combinationCondition : combinationConditionEntityList) {
                combinationCondition.setCouponCondition(conditionEntity);
            }
            conditionEntityList.add(conditionEntity);
            couponEntity.setConditionList(conditionEntityList);
        }
    }

    private void modifyUsageCondition(List<UsageConditionDto> usageConditionDtoList, CouponEntity couponEntity) {
        List<CouponConditionEntity> conditionEntityList = couponEntity.getConditionList();
        CouponConditionEntity condition = conditionEntityList.stream().filter(cd -> cd.getType() == ConditionType.USAGE).findFirst().orElse(null);

        if (condition == null) {
            if (usageConditionDtoList == null || usageConditionDtoList.isEmpty()) {
                return;
            }
        }
        List<UsageConditionEntity> usageConditionEntityList = condition != null ? condition.getUsageConditionList() : new ArrayList<>();
        List<UsageConditionEntity> newUsageConditionEntityList = new ArrayList<>();
        for (UsageConditionDto usageConditionDto : usageConditionDtoList) {
            boolean needInsert = true;
            for (UsageConditionEntity usageConditionEntity : usageConditionEntityList) {
                if (usageConditionEntity.getType() == usageConditionDto.getType()) {
                    // cap nhat lai du lieu
                    if (usageConditionEntity.getType() == UsageConditionType.QUANTITY && (usageConditionEntity.getValue() == null || usageConditionEntity.getValue() <= 0)) {
                        throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, ErrorConstant.NEED_TO_SEND_QUANTITY_VALUE_FOR_USAGE_CONDITION);
                    }
                    usageConditionEntity.setValue(usageConditionDto.getValue());
                    needInsert = false;
                }
            }
            if (needInsert) {
                UsageConditionEntity newUsage = UsageConditionEntity.builder()
                        .couponCondition(condition)
                        .type(usageConditionDto.getType())
                        .value(usageConditionDto.getValue())
                        .build();
                if (usageConditionDto.getType() == UsageConditionType.QUANTITY && (usageConditionDto.getValue() == null || usageConditionDto.getValue() <= 0)) {
                    throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, ErrorConstant.NEED_TO_SEND_QUANTITY_VALUE_FOR_USAGE_CONDITION);
                }
                newUsageConditionEntityList.add(newUsage);
            }
        }
        usageConditionEntityList.addAll(newUsageConditionEntityList);

        List<UsageConditionEntity> deletedUsageList = new ArrayList<>();
        for (UsageConditionEntity usageConditionEntity : usageConditionEntityList) {
            boolean needDelete = true;
            for (UsageConditionDto usageConditionDto : usageConditionDtoList) {
                if (usageConditionEntity.getType() == usageConditionDto.getType()) {
                    // tìm thấy -> khoong can delete -> chi can cap nhat
                    needDelete = false;
                    break;
                }
            }
            // neu khong tim thay trong user gui len -> xoa
            if (needDelete) {
                deletedUsageList.add(usageConditionEntity);
                usageConditionRepository.delete(usageConditionEntity);
            }
        }
        usageConditionEntityList.removeAll(deletedUsageList);

        // neu cac con trong usage bi xoa het -> xoa luon usage condition
        if (usageConditionEntityList.isEmpty() && condition != null) {
            conditionEntityList.remove(condition);
            couponConditionRepository.delete(condition);
        }

        // neu trong db khong co -> chua ton tai -> tao moi condition
        if (condition == null) {
            CouponConditionEntity conditionEntity = CouponConditionEntity.builder()
                    .type(ConditionType.USAGE)
                    .usageConditionList(usageConditionEntityList)
                    .coupon(couponEntity)
                    .build();
            for (UsageConditionEntity usageCondition : usageConditionEntityList) {
                usageCondition.setCouponCondition(conditionEntity);
            }
            conditionEntityList.add(conditionEntity);
            couponEntity.setConditionList(conditionEntityList);
        }
    }

    @Transactional
    @Override
    public void createOrderCoupon(CreateOrderCouponRequest body) {
        if(isExistedCouponCode(body.getCode())) {
            throw new ShopfeeException(ShopfeeErrorCode.COUPON_CODE_EXISTED);
        }
        CouponEntity couponEntity = modelMapperService.mapClass(body, CouponEntity.class);
        couponEntity.setCouponType(CouponType.ORDER);
        couponEntity.setStatus(CouponStatus.RELEASED);
        couponEntity.setRewardType(CouponRewardType.MONEY);


        MoneyRewardEntity moneyRewardEntity = setMoneyReward(body.getUnitReward(), body.getValueReward(), couponEntity);
//        couponEntity.setUnit(body.getUnitReward());
//        couponEntity.setValue(body.getValueReward());
        couponEntity.setMoneyReward(moneyRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = getCouponConditionList(body.getUsageConditionList(), body.getMinPurchaseCondition(), body.getCombinationConditionList(), null, couponEntity);


        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        System.out.println(couponEntity);
        couponRepository.save(couponEntity);
    }

    @Transactional
    @Override
    public void updateOrderCoupon(UpdateOrderCouponRequest body, String couponId) {
        CouponEntity couponEntity =  couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponId));

        couponEntity.setCouponType(CouponType.ORDER);
        couponEntity.setStatus(body.getStatus());
        couponEntity.setRewardType(CouponRewardType.MONEY);
        couponEntity.setCode(body.getCode());
        couponEntity.setDescription(body.getDescription());
        couponEntity.setStartDate(body.getStartDate());
        couponEntity.setExpirationDate(body.getExpirationDate());

        couponEntity.getMoneyReward().setUnit(body.getUnitReward());
        couponEntity.getMoneyReward().setValue(body.getValueReward());

        // Usage condition
        List<UsageConditionDto> usageConditionDtoList = body.getUsageConditionList();
        modifyUsageCondition(usageConditionDtoList, couponEntity);

        // Min purchase
        MinPurchaseConditionDto minPurchaseConditionDto = body.getMinPurchaseCondition();
        modifyMinPurchaseCondition(couponEntity, minPurchaseConditionDto);

        // Combination
        List<CombinationConditionDto> combinationConditionDtoList = body.getCombinationConditionList();
        modifyCombinationCondition(combinationConditionDtoList, couponEntity);

        // Saving coupon
        couponRepository.save(couponEntity);
    }

    @Transactional
    @Override
    public void createAmountOffProductCoupon(CreateProductMoneyCouponRequest body) {
        if(isExistedCouponCode(body.getCode())) {
            throw new ShopfeeException(ShopfeeErrorCode.COUPON_CODE_EXISTED);
        }
        CouponEntity couponEntity = modelMapperService.mapClass(body, CouponEntity.class);
        couponEntity.setCouponType(CouponType.PRODUCT);
        couponEntity.setStatus(CouponStatus.RELEASED);
        couponEntity.setRewardType(CouponRewardType.MONEY);

        MoneyRewardEntity moneyRewardEntity = setMoneyReward(body.getUnitReward(), body.getValueReward(), couponEntity);
//        couponEntity.setUnit(moneyRewardEntity.getUnit());
//        couponEntity.setValue(moneyRewardEntity.getValue());
        couponEntity.setMoneyReward(moneyRewardEntity);

        List<CouponConditionEntity> couponConditionEntityList = getCouponConditionList(body.getUsageConditionList(), body.getMinPurchaseCondition(), body.getCombinationConditionList(), body.getSubjectConditionList(), couponEntity);
        couponEntity.setConditionList(couponConditionEntityList);

        // Saving coupon
        System.out.println(couponEntity);
        moneyRewardRepository.save(couponEntity);
    }


    @Transactional
    @Override
    public void updateAmountOffProductCoupon(UpdateProductMoneyCouponRequest body, String couponId) {
        CouponEntity couponEntity = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponId));
        couponEntity.setCouponType(CouponType.PRODUCT);
        couponEntity.setStatus(body.getStatus());
        couponEntity.setRewardType(CouponRewardType.MONEY);
        couponEntity.setCode(body.getCode());
        couponEntity.setDescription(body.getDescription());
        couponEntity.setStartDate(body.getStartDate());
        couponEntity.setExpirationDate(body.getExpirationDate());


        couponEntity.getMoneyReward().setUnit(body.getUnitReward());
        couponEntity.getMoneyReward().setValue(body.getValueReward());

        // Usage condition
        List<UsageConditionDto> usageConditionDtoList = body.getUsageConditionList();
        modifyUsageCondition(usageConditionDtoList, couponEntity);

        // Min purchase
        MinPurchaseConditionDto minPurchaseConditionDto = body.getMinPurchaseCondition();
        modifyMinPurchaseCondition(couponEntity, minPurchaseConditionDto);

        // Combination
        List<CombinationConditionDto> combinationConditionDtoList = body.getCombinationConditionList();
        modifyCombinationCondition(combinationConditionDtoList, couponEntity);

        // Subject condition
        List<CouponConditionEntity> conditionEntityList = couponEntity.getConditionList();
        modifySubjectConditionList(body.getSubjectConditionList(), conditionEntityList);

        // Saving coupon
        couponRepository.save(couponEntity);
    }

    @Transactional
    @Override
    public void createGiftProductCoupon(CreateBuyXGetYCouponRequest body) {
        if(isExistedCouponCode(body.getCode())) {
            throw new ShopfeeException(ShopfeeErrorCode.COUPON_CODE_EXISTED);
        }
        CouponEntity couponEntity = modelMapperService.mapClass(body, CouponEntity.class);
        couponEntity.setCouponType(CouponType.PRODUCT);
        couponEntity.setStatus(CouponStatus.RELEASED);
        couponEntity.setRewardType(CouponRewardType.PRODUCT_GIFT);

        List<ProductRewardEntity> productRewardEntityList = new ArrayList<>();
        body.getProductRewardList().forEach(reward -> {
            ProductEntity product = productRepository.findById(reward.getProductId())
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND + reward.getProductId()));
            ProductRewardEntity productRewardEntity = new ProductRewardEntity(product.getName(), product, reward.getProductSize(), reward.getQuantity(), couponEntity);
//                    .product(product)
//                    .productSize(reward.getProductSize())
//                    .productName(product.getName())
//                    .quantity(reward.getQuantity())
//                    .coupon(couponEntity)
//                    .build();
            productRewardEntityList.add(productRewardEntity);
        });
        couponEntity.setProductRewardList(productRewardEntityList);

        List<CouponConditionEntity> couponConditionEntityList = getCouponConditionList(body.getUsageConditionList(), body.getMinPurchaseCondition(), body.getCombinationConditionList(), body.getSubjectConditionList(), couponEntity);

        // Saving coupon
        couponEntity.setConditionList(couponConditionEntityList);
        couponRepository.save(couponEntity);
    }


    @Transactional
    @Override
    public void updateGiftProductCoupon(UpdateBuyXGetYCouponRequest body, String couponId) {
        CouponEntity couponEntity = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponId));
        couponEntity.setCouponType(CouponType.PRODUCT);
        couponEntity.setStatus(body.getStatus());
        couponEntity.setRewardType(CouponRewardType.PRODUCT_GIFT);
        couponEntity.setCode(body.getCode());
        couponEntity.setDescription(body.getDescription());
        couponEntity.setStartDate(body.getStartDate());
        couponEntity.setExpirationDate(body.getExpirationDate());


        List<ProductRewardEntity> productRewardEntityList = new ArrayList<>();

        for (ProductRewardEntity oldProductRewardEntity : couponEntity.getProductRewardList()) {
            productRewardRepository.delete(oldProductRewardEntity);
        }

        body.getProductRewardList().forEach(reward -> {
            ProductEntity product = productRepository.findById(reward.getProductId())
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND + reward.getProductId()));
            ProductRewardEntity productRewardEntity = new ProductRewardEntity(product.getName(), product, reward.getProductSize(), reward.getQuantity(), couponEntity);
            productRewardEntityList.add(productRewardEntity);
        });

        couponEntity.clearAndSetProductRewardList(productRewardEntityList);


        List<CouponConditionEntity> couponConditionEntityList = new ArrayList<>();

        // Usage condition
        List<UsageConditionDto> usageConditionDtoList = body.getUsageConditionList();
        modifyUsageCondition(usageConditionDtoList, couponEntity);

        // Min purchase
        MinPurchaseConditionDto minPurchaseConditionDto = body.getMinPurchaseCondition();
        modifyMinPurchaseCondition(couponEntity, minPurchaseConditionDto);

        // Combination
        List<CombinationConditionDto> combinationConditionDtoList = body.getCombinationConditionList();
        modifyCombinationCondition(combinationConditionDtoList, couponEntity);

        // Subject condition
        List<CouponConditionEntity> conditionEntityList = couponEntity.getConditionList();
        modifySubjectConditionList(body.getSubjectConditionList(), conditionEntityList);


        // Saving coupon
        couponRepository.save(couponEntity);
    }

    private void modifySubjectConditionList(List<SubjectConditionDto> subjectConditionDtoList, List<CouponConditionEntity> conditionEntityList) {
        CouponConditionEntity condition = conditionEntityList.stream().filter(cd -> cd.getType() == ConditionType.SUBJECT).findFirst().orElseThrow(
                () -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.ERROR_DUE_TO_MISSING_DATA_DURING_CREATION_PROCESS)
        );
        for (SubjectConditionEntity subjectConditionEntity : condition.getSubjectConditionList()) {
            subjectConditionRepository.delete(subjectConditionEntity);
        }
        List<SubjectConditionEntity> subjectConditionEntityList = new ArrayList<>();
        subjectConditionDtoList.stream().forEach(subject -> {
            ProductEntity productEntity = productRepository.findById(subject.getObjectId())
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND + subject.getObjectId()));
            SubjectConditionEntity subjectConditionEntity = SubjectConditionEntity.builder()
                    .couponCondition(condition)
                    .productName(productEntity.getName())
                    .value(subject.getValue())
                    .objectId(subject.getObjectId())
                    .build();
            subjectConditionEntityList.add(subjectConditionEntity);
        });
        condition.setSubjectConditionList(subjectConditionEntityList);
    }

    @Override
    public void deleteCoupon(String couponId) {
        CouponEntity couponCollection = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponId));
        if (couponCollection.getStatus() == CouponStatus.UNRELEASED) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY, "Actions cannot be performed on the coupon when it is in the UNRELEASED state");
        }
        couponCollection.setDeleted(true);
        couponRepository.save(couponCollection);
    }

    @Override
    public List<GetReleaseCouponListResponse> getReleaseCouponList(int quantity, CouponType type) {
        String userId = SecurityUtils.getCurrentUserId();
        List<GetReleaseCouponListResponse> response = new ArrayList<>();
        if (type == null) {
            response = GetReleaseCouponListResponse.fromCouponEntityList(couponRepository.getReleaseCouponList(quantity));
        } else {
            response = GetReleaseCouponListResponse.fromCouponEntityList(couponRepository.findByStatusAndCouponTypeAndIsDeletedFalse(CouponStatus.RELEASED, type));
        }

        return response;
    }

    @Override
    public GetReleaseCouponByIdResponse getReleaseCouponById(String couponId) {

        CouponEntity couponEntity = couponRepository.findByIdAndIsDeletedFalse(couponId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponId));
        List<CouponConditionDto> conditionDtoData = new ArrayList<>();
        List<CouponConditionEntity> conditionEntityList = couponEntity.getConditionList();
        for (CouponConditionEntity conditionEntity : conditionEntityList) {
            CouponConditionDto conditionDto = new CouponConditionDto();
            conditionDto.setType(conditionEntity.getType());
            if (conditionEntity.getType() == ConditionType.USAGE) {
                List<UsageConditionEntity> usageConditionEntityList = conditionEntity.getUsageConditionList();
                conditionDto.setUsageConditionList(UsageConditionDto.fromUsageConditionEntityList(usageConditionEntityList));
            } else if (conditionEntity.getType() == ConditionType.SUBJECT) {
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
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponId));
        return GetShippingCouponDetailsByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetOrderCouponDetailByIdResponse getOrderCouponDetailById(String couponId) {
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.ORDER)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponId));
        return GetOrderCouponDetailByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetProductGiftCouponDetailByIdResponse getProductGiftCouponDetailById(String couponId) {
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.PRODUCT)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponId));
        return GetProductGiftCouponDetailByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetAmountOffProductCouponDetailByIdResponse getAmountOffProductCouponDetailById(String couponId) {
        CouponEntity coupon = couponRepository.findByIdAndCouponTypeAndIsDeletedFalse(couponId, CouponType.PRODUCT)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponId));
        return GetAmountOffProductCouponDetailByIdResponse.fromCouponEntity(coupon);
    }

    @Override
    public GetCouponOptionsResponse getCouponListForCartResponse(GetCouponListForCartRequest body) {
        String userId = SecurityUtils.getCurrentUserId();
        GetCouponOptionsResponse data = new GetCouponOptionsResponse();
        List<CouponType> noCombineWithShippingCode = new ArrayList<>();
        List<CouponType> noCombineWithOrderCode = new ArrayList<>();
        List<CouponType> noCombineWithProductCode = new ArrayList<>();

        List<CouponType> shippingNoCombineBy = new ArrayList<>();
        List<CouponType> orderNoCombineBy = new ArrayList<>();
        List<CouponType> productNoCombineBy = new ArrayList<>();
        List<CouponType> couponTypeList = new ArrayList<>();

        if (body.getShippingCouponCode() != null) {
            couponTypeList.add(CouponType.SHIPPING);
            noCombineWithShippingCode = checkAndGetCouponTypeNoCombineListWithCoupon(body.getShippingCouponCode());
        }

        if (body.getOrderCouponCode() != null) {
            couponTypeList.add(CouponType.ORDER);
            noCombineWithOrderCode = checkAndGetCouponTypeNoCombineListWithCoupon(body.getOrderCouponCode());
        }
        if (body.getProductCouponCode() != null) {
            couponTypeList.add(CouponType.PRODUCT);
            noCombineWithProductCode = checkAndGetCouponTypeNoCombineListWithCoupon(body.getProductCouponCode());
        }

        if (noCombineWithShippingCode.contains(CouponType.ORDER)) {
            orderNoCombineBy.add(CouponType.SHIPPING);
        }
        if (noCombineWithShippingCode.contains(CouponType.PRODUCT)) {
            productNoCombineBy.add(CouponType.SHIPPING);
        }

        if (noCombineWithOrderCode.contains(CouponType.SHIPPING)) {
            shippingNoCombineBy.add(CouponType.ORDER);
        }
        if (noCombineWithOrderCode.contains(CouponType.PRODUCT)) {
            productNoCombineBy.add(CouponType.ORDER);
        }
        if (noCombineWithProductCode.contains(CouponType.SHIPPING)) {
            shippingNoCombineBy.add(CouponType.PRODUCT);
        }
        if (noCombineWithProductCode.contains(CouponType.ORDER)) {
            orderNoCombineBy.add(CouponType.PRODUCT);
        }

        data.setShippingNoCombineBy(shippingNoCombineBy);
        data.setOrderNoCombineBy(orderNoCombineBy);
        data.setProductNoCombineBy(productNoCombineBy);

        List<GetCouponOptionsResponse.CouponCard> shippingCouponCard = checkAndGetCouponCard(CouponType.SHIPPING, userId, body, couponTypeList, shippingNoCombineBy.isEmpty(), body.getShippingCouponCode());
        data.setShippingCouponList(shippingCouponCard);
        List<GetCouponOptionsResponse.CouponCard> orderCouponCard = checkAndGetCouponCard(CouponType.ORDER, userId, body, couponTypeList,  orderNoCombineBy.isEmpty(), body.getOrderCouponCode());
        data.setOrderCouponList(orderCouponCard);
        List<GetCouponOptionsResponse.CouponCard> productCouponCard = checkAndGetCouponCard(CouponType.PRODUCT, userId, body, couponTypeList,  productNoCombineBy.isEmpty(), body.getProductCouponCode());
        data.setProductCouponList(productCouponCard);

        return data;
    }

    private List<GetCouponOptionsResponse.CouponCard> checkAndGetCouponCard(
            CouponType couponType, String userId, GetCouponListForCartRequest body,
            List<CouponType> couponTypeListInCart, boolean couponInCartCanCombineWithThisCouponType, String couponCode) {
        List<GetCouponOptionsResponse.CouponCard> couponCardList = new ArrayList<>();
        List<CouponEntity> couponEntityList = couponRepository.findByStatusAndCouponTypeAndIsDeletedFalse(CouponStatus.RELEASED, couponType);
        for (CouponEntity coupon : couponEntityList) {
            GetCouponOptionsResponse.CouponCard couponCard = new GetCouponOptionsResponse.CouponCard();
            couponCard.setCouponId(coupon.getId());
            couponCard.setCode(coupon.getCode());
            couponCard.setValid(true);
            couponCard.setDescription(coupon.getDescription());
            couponCard.setExpirationDate(coupon.getExpirationDate());

            // neu la coupon dang duoc chon => bo qua
            if (couponCode != null && couponCode.equals(coupon.getCode())) {
                couponCard.setMinPurchaseCondition(null);
                couponCardList.add(couponCard);
                continue;
            }
            List<CouponConditionEntity> conditionList = coupon.getConditionList();
            // canCombined la dkien coupon trong card co the combine voi loai couponType khong
            if (!couponInCartCanCombineWithThisCouponType) {
                couponCard.setValid(false);
            } else {
                for (CouponConditionEntity condition : conditionList) {
                    // check USAGE
                    if (condition.getType() == ConditionType.USAGE) {
                        List<UsageConditionEntity> usageConditionList = condition.getUsageConditionList();
                        List<GetCouponOptionsResponse.UsageCondition> usageConditionDataList = new ArrayList<>();
                        for (UsageConditionEntity usageCondition : usageConditionList) {
                            if (usageCondition.getType() == UsageConditionType.QUANTITY) {
                                int usedCount = couponUsedRepository.getUsedCouponCount(coupon.getId());
                                if (usedCount >= usageCondition.getValue()) {
                                    // invalid
                                    usageConditionDataList.add(new GetCouponOptionsResponse.UsageCondition(usageCondition.getType(), 0));
                                    couponCard.setValid(false);
                                }
                            } else if (usageCondition.getType() == UsageConditionType.LIMIT_ONE_FOR_USER) {
                                CouponUsedEntity couponUsed = couponUsedRepository.getCouponUsedByUserIdAndCode(userId, coupon.getId()).orElse(null);
                                if (couponUsed != null) {
                                    usageConditionDataList.add(new GetCouponOptionsResponse.UsageCondition(usageCondition.getType(), null));
                                    couponCard.setValid(false);
                                }
                            }
                        }
                        couponCard.setUsageConditionList(usageConditionDataList);
                    }
                    // check MIN_PURCHASE
                    else if (condition.getType() == ConditionType.MIN_PURCHASE) {
                        MinPurchaseConditionEntity minPurchaseCondition = condition.getMinPurchaseCondition();
                        GetCouponOptionsResponse.MinPurchaseCondition minPurchaseConditionData = new GetCouponOptionsResponse.MinPurchaseCondition();
                        if (body.getTotalItemPrice() < minPurchaseCondition.getValue()) {
                            // invalid
                            minPurchaseConditionData.setValue(minPurchaseCondition.getValue());
                            couponCard.setMinPurchaseCondition(minPurchaseConditionData);
                            couponCard.setValid(false);
                        } else {
                            couponCard.setMinPurchaseCondition(null);
                        }
                    }
                    // check SUBJECT_TYPE
                    else if (condition.getType() == ConditionType.SUBJECT) {
                        List<SubjectConditionEntity> subjectConditionEntityList = condition.getSubjectConditionList();
                        List<GetCouponOptionsResponse.SubjectCondition> subjectConditionDataList = new ArrayList<>();
                        boolean isValid = false;
                        for (SubjectConditionEntity subjectConditionEntity : subjectConditionEntityList) {
                            OrderItemDto item = body.getOrderItemList().stream()
                                    .filter(it -> it.getProductId().equals(subjectConditionEntity.getObjectId()))
                                    .findFirst().orElse(null);
                            if (item == null) {
                                // invalid
                                ProductEntity productEntity = productRepository.findById(subjectConditionEntity.getObjectId())
                                        .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND + subjectConditionEntity.getObjectId()));
                                subjectConditionDataList.add(new GetCouponOptionsResponse.SubjectCondition(productEntity.getName(), subjectConditionEntity.getValue()));

                            } else {
                                int count = 0;
                                for (ItemDetailDto itemDetailDto : item.getItemDetailList()) {
                                    count += itemDetailDto.getQuantity();
                                }
                                if (count < subjectConditionEntity.getValue()) {
                                    // invalid
                                    ProductEntity productEntity = productRepository.findById(subjectConditionEntity.getObjectId())
                                            .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND + subjectConditionEntity.getObjectId()));
                                    subjectConditionDataList.add(new GetCouponOptionsResponse.SubjectCondition(productEntity.getName(), subjectConditionEntity.getValue()));

                                } else {
                                    isValid = true;
                                    break;
                                }
                            }
                        }

                        if (isValid) {
                            couponCard.setSubjectConditionList(null);
                        } else {
                            couponCard.setValid(false);
                            couponCard.setSubjectConditionList(subjectConditionDataList);
                        }
                    }
                    // check COMBINATION
                    else if (condition.getType() == ConditionType.COMBINATION) {
                        List<CouponType> combinationList = combinationConditionRepository.getCombinationConditionListByCouponCode(coupon.getCode());
                        List<GetCouponOptionsResponse.CombinationCondition> combinationConditionDataList = new ArrayList<>();
                        // check combination xem 1 coupon trong list không thể combination với coupon nào trong cart
                        for (CouponType couponTypeInCart : couponTypeListInCart) {
                            // neu cac coupon list khong chua loai couponType trong cart => khong the combine tu list -> cart
                            if (!combinationList.contains(couponTypeInCart) && coupon.getCouponType() != couponTypeInCart) {
                                // invalid
                                combinationConditionDataList.add(new GetCouponOptionsResponse.CombinationCondition(couponTypeInCart));
                                couponCard.setValid(false);
                            }
                        }
                        couponCard.setCombinationConditionList(combinationConditionDataList);
                    }
                }
            }
            couponCardList.add(couponCard);
        }
        return couponCardList;
    }

    private List<CouponType> checkAndGetCouponTypeNoCombineListWithCoupon(String couponCode) {
        List<CouponType> couponTypeList = Arrays.asList(CouponType.SHIPPING, CouponType.PRODUCT, CouponType.ORDER);
        List<CouponType> data = new ArrayList<>();

        if (couponCode != null) {
            CouponEntity couponEntity = couponRepository.findByCodeAndStatusAndIsDeletedFalse(couponCode, CouponStatus.RELEASED)
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponCode));

            List<CouponType> couponTypeCombinedListOfCoupon = combinationConditionRepository.getCombinationConditionListByCouponCode(couponCode);

            for (CouponType couponType : couponTypeList) {

                if (!couponTypeCombinedListOfCoupon.contains(couponType) && couponType != couponEntity.getCouponType()) {
                    data.add(couponType);
                }
            }
        }
        return data;
    }

    @Override
    public List<CheckCouponInCartResponse> validateCouponAndItemInCart(GetCouponListForCartRequest body) {
        List<CheckCouponInCartResponse> data = new ArrayList<>();
        String userId = SecurityUtils.getCurrentUserId();
        List<OrderItemDto> orderItemDtoList = body.getOrderItemList();
        if (body.getOrderCouponCode() != null) {
            data.add(checkCouponCodeWithOrderItemCart(body.getOrderCouponCode(), body.getTotalPayment(), userId, orderItemDtoList));
        }
        if (body.getProductCouponCode() != null) {
            data.add(checkCouponCodeWithOrderItemCart(body.getProductCouponCode(), body.getTotalPayment(), userId, orderItemDtoList));
        }
        if (body.getShippingCouponCode() != null) {
            data.add(checkCouponCodeWithOrderItemCart(body.getShippingCouponCode(), body.getTotalPayment(), userId, orderItemDtoList));
        }
        return data;
    }

    private CheckCouponInCartResponse checkCouponCodeWithOrderItemCart(String couponCode, long totalPayment, String userId, List<OrderItemDto> orderItemDtoList) {
        CheckCouponInCartResponse couponResult = new CheckCouponInCartResponse();
        CouponEntity couponEntity = couponRepository.findByCodeAndStatusAndIsDeletedFalse(couponCode, CouponStatus.RELEASED)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponCode));
        couponResult.setCouponType(couponEntity.getCouponType());

        CheckCouponInCartResponse.ViolatedCondition violatedCondition = new CheckCouponInCartResponse.ViolatedCondition();

        List<CouponConditionEntity> couponConditionEntityList = couponEntity.getConditionList();
        List<String> productIdListInCart = new ArrayList<>();
        List<String> subjectIdListInCart = new ArrayList<>();

        for (CouponConditionEntity conditionEntity : couponConditionEntityList) {
            ConditionType conditionType = conditionEntity.getType();
            // MIN_PURCHASE
            if (conditionType == ConditionType.MIN_PURCHASE) {
                long minPurchaseValue = conditionEntity.getMinPurchaseCondition().getValue();
                if (totalPayment < minPurchaseValue) {
                    // invalid
                    couponResult.setValid(false);
                    CheckCouponInCartResponse.MinPurchaseCondition minPurchaseCondition = new CheckCouponInCartResponse.MinPurchaseCondition(minPurchaseValue);
                    violatedCondition.setMinPurchaseCondition(minPurchaseCondition);
                }
            }
            // USAGE
            else if (conditionType == ConditionType.USAGE) {
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
            // SUBJECT
            else if (conditionType == ConditionType.SUBJECT) {
                List<SubjectConditionEntity> subjectConditionEntityList = conditionEntity.getSubjectConditionList();

                List<CheckCouponInCartResponse.SubjectCondition> subjectConditionList = new ArrayList<>();
                productIdListInCart = orderItemDtoList.stream().map(OrderItemDto::getProductId).toList();
                subjectIdListInCart = subjectConditionEntityList.stream().map(SubjectConditionEntity::getObjectId).toList();

                boolean isValid = false;
                for (SubjectConditionEntity subjectConditionEntity : subjectConditionEntityList) {
                    OrderItemDto item = orderItemDtoList.stream()
                            .filter(it -> it.getProductId().equals(subjectConditionEntity.getObjectId()))
                            .findFirst().orElse(null);
                    ProductEntity productEntity = productRepository.findById(subjectConditionEntity.getObjectId())
                            .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND + subjectConditionEntity.getObjectId()));
                    // trong cart khong co product item thoa man
                    if (item == null) {
                        // invalid
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
                            subjectConditionList.add(new CheckCouponInCartResponse.SubjectCondition(productEntity.getName(), subjectConditionEntity.getValue()));
                        } else {
                            isValid = true;
                            break;
                        }
                    }
                }
                if (isValid) {
                    violatedCondition.setSubjectConditionList(null);
                } else {
                    couponResult.setValid(false);
                    violatedCondition.setSubjectConditionList(subjectConditionList);
                }
            }
        }
        if (couponResult.isValid()) {
            CheckCouponInCartResponse.Reward reward = new CheckCouponInCartResponse.Reward();

            if (couponEntity.getRewardType() == CouponRewardType.MONEY) {
                reward.setMoneyReward(CheckCouponInCartResponse.fromMoneyRewardEntity(couponEntity.getMoneyReward()));
            } else if (couponEntity.getRewardType() == CouponRewardType.PRODUCT_GIFT) {
                reward.setProductRewardList(CheckCouponInCartResponse.fromProductRewardEntityList(couponEntity.getProductRewardList()));
            }
            if (couponEntity.getCouponType() == CouponType.PRODUCT) {
                for (String subjectId : subjectIdListInCart) {
                    if (productIdListInCart.contains(subjectId)) {
                        ProductEntity productEntity = productRepository.findById(subjectId)
                                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND + subjectId));

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

}
