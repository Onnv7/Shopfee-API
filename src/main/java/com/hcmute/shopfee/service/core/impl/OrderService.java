package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.ItemDetailDto;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.dto.request.CreateOnsiteOrderRequest;
import com.hcmute.shopfee.dto.request.CreateShippingOrderRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.entity.database.*;
import com.hcmute.shopfee.entity.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.database.coupon.condition.CombinationConditionEntity;
import com.hcmute.shopfee.entity.database.coupon.condition.MinPurchaseConditionEntity;
import com.hcmute.shopfee.entity.database.coupon.condition.SubjectConditionEntity;
import com.hcmute.shopfee.entity.database.coupon.condition.UsageConditionEntity;
import com.hcmute.shopfee.entity.database.coupon_used.CouponRewardReceivedEntity;
import com.hcmute.shopfee.entity.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.database.coupon_used.reward.MoneyRewardReceivedEntity;
import com.hcmute.shopfee.entity.database.coupon_used.reward.ProductRewardReceivedEntity;
import com.hcmute.shopfee.entity.database.order.*;
import com.hcmute.shopfee.entity.database.product.ProductEntity;
import com.hcmute.shopfee.entity.database.product.SizeEntity;
import com.hcmute.shopfee.entity.database.product.ToppingEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.entity.elasticsearch.OrderIndex;
import com.hcmute.shopfee.repository.database.*;
import com.hcmute.shopfee.repository.database.coupon.CouponConditionRepository;
import com.hcmute.shopfee.repository.database.coupon.CouponRepository;
import com.hcmute.shopfee.repository.database.coupon.condition.UsageConditionRepository;
import com.hcmute.shopfee.repository.database.coupon_used.CouponUsedRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.service.core.IOrderService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.elasticsearch.OrderSearchService;
import com.hcmute.shopfee.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.hcmute.shopfee.constant.VNPayConstant.*;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderBillRepository orderBillRepository;
    private final ModelMapperService modelMapperService;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AddressRepository addressRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final OrderSearchService orderSearchService;
    private final EmployeeRepository employeeRepository;
    private final OrderEventRepository orderEventRepository;
    private final CouponRepository couponRepository;
    private final CouponUsedRepository couponUsedRepository;
    private final CouponConditionRepository couponConditionRepository;
    private final CategoryRepository categoryRepository;
    private final UsageConditionRepository usageConditionRepository;

    public BranchEntity getNearestBranches(AddressEntity address) {
        // TODO: xem có status thì check status cửa hàng
        List<BranchEntity> allBranches = branchRepository.findAll();

        allBranches.sort(Comparator.comparingDouble(branch ->
                CalculateUtils.calculateDistance(address.getLatitude(), address.getLongitude(), branch.getLatitude(), branch.getLongitude())));
        return allBranches.subList(0, Math.min(1, allBranches.size())).get(0);

    }

    private Map<String, Object> buildTransaction(PaymentType paymentType, HttpServletRequest request, long totalPrice) {
        TransactionEntity transData = new TransactionEntity();
        Map<String, Object> result = new HashMap<>();
        if (paymentType == PaymentType.CASHING) {
            transData = TransactionEntity.builder()
                    .status(PaymentStatus.UNPAID)
                    .totalPaid(0L)
                    .paymentType(PaymentType.CASHING).build();
        } else if (paymentType == PaymentType.BANKING_VNPAY) {
            Map<String, String> paymentData = null;
            try {
                paymentData = VNPayUtils.createUrlPayment(request, totalPrice, "Shipping Order Info");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            transData = TransactionEntity.builder()
                    .invoiceCode(paymentData.get(VNP_TXN_REF_KEY))
                    .timeCode(paymentData.get(VNP_CREATE_DATE_KEY))
                    .status(PaymentStatus.UNPAID)
                    .paymentType(PaymentType.BANKING_VNPAY)
                    .totalPaid(0L)
                    .build();
            result.put(VNP_URL_KEY, paymentData.get(VNP_URL_KEY));
        }
        result.put("transaction", transData);
        return result;
    }

    private long calculateOrderBill(List<OrderItemDto> orderItemList, OrderBillEntity orderBill) {
        long totalPrice = 0;
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        int itemSize = orderItemList.size();
        for (int i = 0; i < itemSize; i++) {
            OrderItemDto orderItemDto = orderItemList.get(i);

            ProductEntity productInfo = productRepository.findByIdAndStatusAndIsDeletedFalse(orderItemDto.getProductId(), ProductStatus.AVAILABLE)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + orderItemDto.getProductId()));

            OrderItemEntity item = new OrderItemEntity(); // modelMapperService.mapClass(orderItemDto, OrderItemEntity.class);
            item.setProduct(productInfo);
            item.setName(productInfo.getName());


            long totalPriceToppings = 0;
            item.setName(productInfo.getName());

            List<ToppingEntity> toppingList = productInfo.getToppingList();
            List<SizeEntity> sizeList = productInfo.getSizeList();

            List<ItemToppingEntity> itemsToppingList = new ArrayList<>();
            List<ItemDetailEntity> itemsDetailEntityList = new ArrayList<>();

            for (ItemDetailDto itemDetail : orderItemDto.getItemDetailList()) {
                List<String> toppingNameList = itemDetail.getToppingNameList() != null ? itemDetail.getToppingNameList() : new ArrayList<>();

                // Khởi tạo và set các thuộc tính cơ bản
                ItemDetailEntity itemDetailEntity = new ItemDetailEntity();
                itemDetailEntity.setOrderItem(item);

                itemDetailEntity.setNote(itemDetail.getNote());
                itemDetailEntity.setQuantity(itemDetail.getQuantity());
                itemDetailEntity.setSize(itemDetail.getSize());

                // set topping list cho item detail
                for (String toppingName : toppingNameList) {
                    ItemToppingEntity itemTopping = new ItemToppingEntity();
                    ToppingEntity toppingEntity = toppingList.stream()
                            .filter(topping -> toppingName.equals(topping.getName()))
                            .findFirst()
                            .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + toppingName));
                    itemTopping.setName(toppingName);
                    itemTopping.setPrice(toppingEntity.getPrice());
                    itemTopping.setItemDetail(itemDetailEntity);
                    itemsToppingList.add(itemTopping);
                    totalPriceToppings += toppingEntity.getPrice();
                }
                itemDetailEntity.setItemToppingList(itemsToppingList);

                // set size
                SizeEntity sizeItem = sizeList.stream()
                        .filter(it -> it.getSize() == itemDetail.getSize())
                        .findFirst().orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + itemDetail.getSize()));
                itemDetailEntity.setPrice(sizeItem.getPrice());
                itemsDetailEntityList.add(itemDetailEntity);

                totalPrice += (long) ((sizeItem.getPrice() + totalPriceToppings) * itemDetail.getQuantity());
            }

            item.setItemDetailList(itemsDetailEntityList);

            item.setOrderBill(orderBill);
            orderItemEntityList.add(item);
        }

        orderBill.setOrderItemList(orderItemEntityList);

        return totalPrice;
    }



//    private boolean checkMinPurchaseCondition(MinPurchaseConditionEntity minPurchaseCondition, long totalOrderBill, int itemCount) {
//        if (minPurchaseCondition.getType() == MiniPurchaseType.NONE) {
//            return true;
//        } else if (minPurchaseCondition.getType() == MiniPurchaseType.MONEY) {
//            if (totalOrderBill < minPurchaseCondition.getValue()) {
//                return false;
//            }
//        } else {
//            if (itemCount < minPurchaseCondition.getValue()) {
//                return false;
//            }
//        }
//        return true;
//    }

    private boolean checkUsageCondition(String userId, String couponCode, List<UsageConditionEntity> usageConditionList) {
        CouponEntity coupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(couponCode, CouponStatus.RELEASED)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponCode));

        for (UsageConditionEntity usageCondition : usageConditionList) {
            UsageConditionType type = usageCondition.getType();
            switch (type) {
                case QUANTITY -> {
                    int usedCount = couponUsedRepository.getUsedCouponCount(coupon.getId());
                    if (usageCondition.getValue() <= usedCount) {
                        return false;
                    }
                }
                case LIMIT_ONE_FOR_USER -> {
                    CouponUsedEntity couponUsed = couponUsedRepository.getCouponUsedByUserIdAndCode(userId, coupon.getId())
                            .orElse(null);
                    if (couponUsed != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

//    private boolean checkCombinationCondition(List<String> couponCodeList) {
//        List<CouponEntity> couponEntityList = new ArrayList<>();
//        // tìm và thêm coupon vào list
//        for (String couponCode : couponCodeList) {
//            CouponEntity couponEntity = couponRepository.findByCodeAndStatusAndIsDeletedFalse(couponCode, CouponStatus.RELEASED)
//                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponCode));
//            couponEntityList.add(couponEntity);
//        }
//        CouponType[] types = {CouponType.ORDER, CouponType.PRODUCT, CouponType.SHIPPING};
//
//        // tạo mảng thống kê với cột là coupon, hàng là loại coupon có thể combination
//        int[][] array = new int[3][3];
//        // thống kê coupon vào mảng trên
//        for (CouponEntity coupon : couponEntityList) {
//            CouponType couponType = coupon.getCouponType();
//            int col = couponType == CouponType.ORDER ? 0 :
//                    couponType == CouponType.PRODUCT ? 1 : 2;
//            for (CouponConditionEntity condition : coupon.getConditionList()) {
//                if (condition.getType() != ConditionType.COMBINATION) {
//                    continue;
//                }
//                List<CombinationConditionEntity> combinationList = condition.getCombinationConditionList();
//                if (combinationList != null) {
//                    combinationList.forEach(combination -> {
//                        CombinationType combinationType = combination.getType();
//                        int row = combinationType == CombinationType.ORDER ? 0 :
//                                combinationType == CombinationType.PRODUCT ? 1 : 2;
//                        array[row][col]++;
//                    });
//                }
//            }
//        }
//
//        // đánh giá thống kê: đường chéo dấu huyền
//        for (CouponType type : types) {
//            List<CouponEntity> sameCouponeList = couponEntityList.stream()
//                    .filter(coupon -> coupon.getCouponType() == type)
//                    .toList();
//            if (sameCouponeList.size() <= 1) {
//                continue;
//            }
//            if ((type == CouponType.ORDER && array[0][0] != sameCouponeList.size()) ||
//                    (type == CouponType.PRODUCT && array[1][1] != sameCouponeList.size()) ||
//                    (type == CouponType.SHIPPING && array[2][2] != sameCouponeList.size())
//            ) {
//                return false;
//            }
//
//        }
//
//
//        List<CouponEntity> orderCouponeList = couponEntityList.stream()
//                .filter(coupon -> coupon.getCouponType() == CouponType.ORDER)
//                .toList();
//        List<CouponEntity> productCouponList = couponEntityList.stream()
//                .filter(coupon -> coupon.getCouponType() == CouponType.PRODUCT)
//                .toList();
//        List<CouponEntity> shippingCouponList = couponEntityList.stream()
//                .filter(coupon -> coupon.getCouponType() == CouponType.SHIPPING)
//                .toList();
//        if (orderCouponeList.size() * productCouponList.size() > 0) {
//            if (orderCouponeList.size() != array[1][0] || productCouponList.size() != array[0][1]) {
//                return false;
//            }
//        }
//        if (orderCouponeList.size() * shippingCouponList.size() > 0) {
//            if (orderCouponeList.size() != array[2][0] || shippingCouponList.size() != array[0][2]) {
//                return false;
//            }
//        }
//        if (productCouponList.size() * shippingCouponList.size() > 0) {
//            if (productCouponList.size() != array[2][1] || shippingCouponList.size() != array[1][2]) {
//                return false;
//            }
//        }
//
//        return true;
//    }

//    private boolean checkSubjectCondition(List<SubjectConditionEntity> conditions, MinPurchaseConditionEntity minPurchaseCondition, List<OrderItemDto> orderItemList) {
//        for (SubjectConditionEntity condition : conditions) {
//            if (condition.getType() == TargetType.PRODUCT) {
//                OrderItemDto item = orderItemList.stream().filter(it -> it.getProductId().equals(condition.getValue())).findFirst().orElse(null);
//                if (item == null) {
//                    continue;
//                }
//                if (minPurchaseCondition.getType() == MiniPurchaseType.NONE) {
//                    return true;
//                } else if (minPurchaseCondition.getType() == MiniPurchaseType.QUANTITY) {
//                    int quantity = 0;
//                    for (ItemDetailDto detail : item.getItemDetailList()) {
//                        quantity += detail.getQuantity();
//                    }
//                    if (quantity >= minPurchaseCondition.getValue()) {
//                        return true;
//                    }
//                } else if (minPurchaseCondition.getType() == MiniPurchaseType.MONEY) {
//                    ProductEntity product = productRepository.findByIdAndIsDeletedFalse(item.getProductId())
//                            .orElse(null);
//                    if (product == null) {
//                        return false;
//                    }
//                    long money = 0;
//                    for (ItemDetailDto detail : item.getItemDetailList()) {
//                        money = money + detail.getQuantity() * product.getPrice();
//                    }
//                    if (money >= minPurchaseCondition.getValue()) {
//                        return true;
//                    }
//                }
//            } else if (condition.getType() == TargetType.CATEGORY) {
//                OrderItemDto item = orderItemList.stream()
//                        .filter(it -> {
//                            ProductEntity product = productRepository.findByIdAndIsDeletedFalse(it.getProductId()).orElse(null);
//                            if (product == null) {
//                                return false;
//                            }
//                            return product.getCategory().getId().equals(condition.getValue());
//                        }).findFirst()
//                        .orElse(null);
//                if (item == null) {
//                    continue;
//                }
//                if (minPurchaseCondition.getType() == MiniPurchaseType.NONE) {
//                    return true;
//                } else if (minPurchaseCondition.getType() == MiniPurchaseType.QUANTITY) {
//                    int quantity = 0;
//                    for (ItemDetailDto detail : item.getItemDetailList()) {
//                        quantity += detail.getQuantity();
//                    }
//
//                    if (quantity >= minPurchaseCondition.getValue()) {
//                        return true;
//                    }
//                } else if (minPurchaseCondition.getType() == MiniPurchaseType.MONEY) {
//                    ProductEntity product = productRepository.findByIdAndIsDeletedFalse(item.getProductId())
//                            .orElse(null);
//                    if (product == null) {
//                        return false;
//                    }
//                    long money = 0;
//                    for (ItemDetailDto detail : item.getItemDetailList()) {
//                        money = money + detail.getQuantity() * product.getPrice();
//                    }
//
//                    if (money >= minPurchaseCondition.getValue()) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return true;
//    }

//    public void validateCoupon(String couponCode, List<OrderItemDto> orderItemList, long total, int itemSize) {
//        CouponEntity coupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(couponCode, CouponStatus.RELEASED)
//                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponCode));
//
//        Date currentTime = new Date();
//
//        if(coupon.getStartDate().after(currentTime) || coupon.getExpirationDate().before(currentTime)) {
//            throw new CustomException(ErrorConstant.COUPON_EXPIRED);
//        }
//
//        coupon.getConditionList().forEach(it -> {
//            if (it.getUsageConditionList() != null) {
//                if (!checkUsageCondition(SecurityUtils.getCurrentUserId(), coupon.getCode(), it.getUsageConditionList())) {
//                    throw new CustomException(ErrorConstant.COUPON_INVALID);
//                }
//            } else if (it.getMinPurchaseCondition() != null && coupon.getCouponType() != CouponType.PRODUCT) {
//                if (!checkMinPurchaseCondition(it.getMinPurchaseCondition(), total, itemSize)) {
//                    throw new CustomException(ErrorConstant.COUPON_INVALID);
//                }
//            } else if (it.getSubjectConditionList() != null && coupon.getCouponType() == CouponType.PRODUCT) {
//                if (!checkSubjectCondition(it.getSubjectConditionList(), it.getMinPurchaseCondition(), orderItemList)) {
//                    throw new CustomException(ErrorConstant.COUPON_INVALID);
//                }
//            }
//        });
//    }

//    public void validateCouponForOrder(long totalItemPrice, List<OrderItemDto> itemList, String orderCouponCode, String shippingCouponCode, String productCouponCode) {
//        List<String> couponCodeList = new ArrayList<>();
//        if (orderCouponCode != null) {
//            couponCodeList.add(orderCouponCode);
//            validateCoupon(orderCouponCode, itemList, totalItemPrice, itemList.size());
//        }
//        if (shippingCouponCode != null) {
//            couponCodeList.add(shippingCouponCode);
//            validateCoupon(shippingCouponCode, itemList, totalItemPrice, itemList.size());
//        }
//        if (productCouponCode != null) {
//            couponCodeList.add(productCouponCode);
//            validateCoupon(productCouponCode, itemList, totalItemPrice, itemList.size());
//        }
//        if (!checkCombinationCondition(couponCodeList)) {
//            throw new CustomException(ErrorConstant.COUPON_INVALID);
//        }
//    }


    private CouponUsedEntity createCouponUsedEntity(String couponCode) {
        CouponEntity coupon = couponRepository.findByCodeAndIsDeletedFalse(couponCode)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponCode));

        CouponUsedEntity couponUsed = CouponUsedEntity.builder()
                .code(couponCode)
                .coupon(coupon)
                .type(CouponType.ORDER)
                .build();
        CouponRewardReceivedEntity couponRewardReceived = new CouponRewardReceivedEntity();

        if (coupon.getCouponReward().getType() == CouponRewardType.MONEY) {
            MoneyRewardReceivedEntity moneyRewardReceived = MoneyRewardReceivedEntity.builder()
                    .unit(coupon.getCouponReward().getMoneyReward().getUnit())
                    .value(coupon.getCouponReward().getMoneyReward().getValue())
                    .couponRewardReceived(couponRewardReceived)
                    .build();
            couponRewardReceived.setMoneyRewardReceived(moneyRewardReceived);
            couponRewardReceived.setType(CouponRewardType.MONEY);
            couponRewardReceived.setCouponUsed(couponUsed);
        } else if (coupon.getCouponReward().getType() == CouponRewardType.PRODUCT_GIFT) {
            List<ProductRewardReceivedEntity> productRewardList = new ArrayList<>();
            coupon.getCouponReward().getProductRewardList().forEach(productGift -> {

                ProductRewardReceivedEntity productRewardReceived = ProductRewardReceivedEntity.builder()
                        .productId(productGift.getProductId())
                        .couponRewardReceived(couponRewardReceived)
                        .productSize(productGift.getProductSize())
                        .quantity(productGift.getQuantity())
                        .productName(productGift.getProductName())
                        .build();
                productRewardList.add(productRewardReceived);
            });
            couponRewardReceived.setType(CouponRewardType.PRODUCT_GIFT);
            couponRewardReceived.setCouponUsed(couponUsed);
            couponRewardReceived.setProductRewardReceivedList(productRewardList);
        }
        couponUsed.setCouponRewardReceived(couponRewardReceived);
        return couponUsed;
    }

    public long applyCouponForOrder(OrderBillEntity orderBill, String orderCouponCode, String shippingCouponCode, String productCouponCode) {
        long amountReduced = 0L;
        List<CouponUsedEntity> couponUsedList = new ArrayList<>();

        if (orderCouponCode != null) {
            CouponUsedEntity couponUsed = createCouponUsedEntity(orderCouponCode);
            couponUsed.setOrderBill(orderBill);
            couponUsedList.add(couponUsed);
            if (couponUsed.getCouponRewardReceived().getType() == CouponRewardType.MONEY) {
                if (couponUsed.getCouponRewardReceived().getMoneyRewardReceived().getUnit() == MoneyRewardUnit.MONEY) {
                    amountReduced += couponUsed.getCouponRewardReceived().getMoneyRewardReceived().getValue();
                } else if (couponUsed.getCouponRewardReceived().getMoneyRewardReceived().getUnit() == MoneyRewardUnit.PERCENTAGE) {
                    amountReduced += orderBill.getTotalItemPrice() * couponUsed.getCouponRewardReceived().getMoneyRewardReceived().getValue() / 100;
                }
            }
        }
        if (shippingCouponCode != null) {
            CouponUsedEntity couponUsed = createCouponUsedEntity(shippingCouponCode);
            couponUsed.setOrderBill(orderBill);
            couponUsedList.add(couponUsed);
            amountReduced += orderBill.getShippingFee();
        }
        if (productCouponCode != null) {
            CouponUsedEntity couponUsed = createCouponUsedEntity(productCouponCode);
            couponUsed.setOrderBill(orderBill);
            couponUsedList.add(couponUsed);
            if (couponUsed.getCouponRewardReceived().getType() == CouponRewardType.MONEY) {
                if (couponUsed.getCouponRewardReceived().getMoneyRewardReceived().getUnit() == MoneyRewardUnit.MONEY) {
                    amountReduced += couponUsed.getCouponRewardReceived().getMoneyRewardReceived().getValue();
                } else if (couponUsed.getCouponRewardReceived().getMoneyRewardReceived().getUnit() == MoneyRewardUnit.PERCENTAGE) {
                    amountReduced += orderBill.getTotalItemPrice() * couponUsed.getCouponRewardReceived().getMoneyRewardReceived().getValue() / 100;
                }
            }
        }
        orderBill.setCouponUsedList(couponUsedList);
        return amountReduced;
    }

    @Transactional
    @Override
    public CreateOrderResponse createShippingOrder(CreateShippingOrderRequest body, HttpServletRequest request) {
        SecurityUtils.checkUserId(body.getUserId());
        long totalPrice = 0L;
        String userId = SecurityUtils.getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + userId));
        if(user.getCoin() < body.getCoin()) {
            throw new CustomException(ErrorConstant.INVALID_COIN_NUMBER);
        }

        OrderBillEntity orderBill = modelMapperService.mapClass(body, OrderBillEntity.class);
        orderBill.setUser(user);
        orderBill.setOrderType(OrderType.SHIPPING);

        totalPrice = calculateOrderBill(body.getItemList(), orderBill);

        // set địa chỉ giao hàng
        AddressEntity address = addressRepository.findById(body.getAddressId())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getAddressId()));
        ShippingInformationEntity shippingInformation = new ShippingInformationEntity();
        shippingInformation.fromAddressEntity(address);

        shippingInformation.setOrderBill(orderBill);
        orderBill.setShippingInformation(shippingInformation);

        // set sự kiện đơn hàng
        List<OrderEventEntity> orderEventList = new ArrayList<>();
        orderEventList.add(OrderEventEntity.builder()
                .orderStatus(OrderStatus.CREATED)
                .description("Order created successfully")
                .orderBill(orderBill)
                .isEmployee(false)
                .build());
        orderBill.setOrderEventList(orderEventList);

        // set chi nhánh xử lý đơn
        BranchEntity branch = getNearestBranches(address);
        orderBill.setBranch(branch);

        // set tổng hóa đơn và phí ship
        orderBill.setShippingFee(body.getShippingFee());
        orderBill.setTotalItemPrice(totalPrice);

        // tính phí vận chuyển
        totalPrice += body.getShippingFee();

        // xử lý coupon
//        validateCouponForOrder(totalPrice, body.getItemList(), body.getOrderCouponCode(), body.getShippingCouponCode(), body.getProductCouponCode());
        long amountReduced = applyCouponForOrder(orderBill, body.getOrderCouponCode(), body.getShippingCouponCode(), body.getProductCouponCode());

        totalPrice -= amountReduced;

        if(totalPrice < body.getCoin()) {
            orderBill.setCoin(totalPrice);
            totalPrice = 0;
        } else {
            totalPrice -= body.getCoin();
            orderBill.setCoin(body.getCoin());
        }
        user.setCoin(orderBill.getCoin());
        userRepository.save(user);

        if (totalPrice != body.getTotal()) {
            throw new CustomException(ErrorConstant.TOTAL_ORDER_INVALID);
        }
        orderBill.setTotalPayment(totalPrice);

        // set giao dịch
        Map<String, Object> transactionBuilderMap = buildTransaction(body.getPaymentType(), request, totalPrice);
        TransactionEntity transaction = (TransactionEntity) transactionBuilderMap.get("transaction");
        transaction.setOrderBill(orderBill);
        orderBill.setTransaction(transaction);

        orderBillRepository.save(orderBill);

        CreateOrderResponse resData = CreateOrderResponse.builder()
                .orderId(orderBill.getId())
                .transactionId(transaction.getId())
                .build();
        if (transactionBuilderMap.get(VNP_URL_KEY) != null && totalPrice > 0) {
            resData.setPaymentUrl(transactionBuilderMap.get(VNP_URL_KEY).toString());
        }
        return resData;
    }

    @Transactional
    @Override
    public CreateOrderResponse createOnsiteOrder(CreateOnsiteOrderRequest body, HttpServletRequest request) {
        SecurityUtils.checkUserId(body.getUserId());
        long totalPrice = 0L;
        String userId = SecurityUtils.getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + userId));
        OrderBillEntity orderBill = modelMapperService.mapClass(body, OrderBillEntity.class);
        orderBill.setUser(user);
        orderBill.setOrderType(OrderType.ONSITE);

        totalPrice = calculateOrderBill(body.getItemList(), orderBill);
        orderBill.setTotalItemPrice(totalPrice);

//        validateCouponForOrder(totalPrice, body.getItemList(), body.getOrderCouponCode(), null, body.getProductCouponCode());
        long amountReduced = applyCouponForOrder(orderBill, body.getOrderCouponCode(), null, body.getProductCouponCode());

        totalPrice -= amountReduced;

        if(totalPrice < body.getCoin()) {
            orderBill.setCoin(totalPrice);
            totalPrice = 0;
        } else {
            totalPrice -= body.getCoin();
            orderBill.setCoin(body.getCoin());
        }
        user.setCoin(orderBill.getCoin());
        userRepository.save(user);

        if (totalPrice != body.getTotal()) {
            throw new CustomException(ErrorConstant.TOTAL_ORDER_INVALID);
        }

        // set giao dịch
        Map<String, Object> transactionBuilderMap = buildTransaction(body.getPaymentType(), request, totalPrice);
//        transaction = transactionRepository.save(transaction);
        TransactionEntity transaction = (TransactionEntity) transactionBuilderMap.get("transaction");
        transaction.setOrderBill(orderBill);
        orderBill.setTransaction(transaction);

        // set sự kiện đơn hàng
        List<OrderEventEntity> orderEventList = new ArrayList<>();
        orderEventList.add(OrderEventEntity.builder()
                .orderStatus(OrderStatus.CREATED)
                .description("Order created successfully")
                .isEmployee(false)
                .orderBill(orderBill)
                .build());
        orderBill.setOrderEventList(orderEventList);

        // set chi nhánh đặt hàng
        BranchEntity branch = branchRepository.findById(body.getBranchId())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getBranchId()));
        orderBill.setBranch(branch);

        // set thời gian nhận hàng
        orderBill.setReceiveTime(body.getReceiveTime());

        orderBillRepository.save(orderBill);

        CreateOrderResponse resData = CreateOrderResponse.builder()
                .orderId(orderBill.getId())
                .transactionId(transaction.getId())
                .build();
        if (transactionBuilderMap.get(VNP_URL_KEY) != null && totalPrice > 0) {
            resData.setPaymentUrl(transactionBuilderMap.get(VNP_URL_KEY).toString());
        }
        return resData;
    }

    @Override
    public List<GetOrderHistoryForEmployeeResponse> getOrderHistoryPageForEmployee(OrderStatus orderStatus, int page, int size, String key) {
        String statusRegex = RegexUtils.generateFilterRegexString(orderStatus != null ? orderStatus.toString() : "");
        if (key != null) {
            List<OrderIndex> orderList = orderSearchService.searchOrderForAdmin(key, page, size, statusRegex).getContent();
            return modelMapperService.mapList(orderList, GetOrderHistoryForEmployeeResponse.class);
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        List<OrderBillEntity> orderList = orderBillRepository.getOrderBillByLastStatus(orderStatus.name(), pageable).getContent();
        List<GetOrderHistoryForEmployeeResponse> resData = new ArrayList<>();
        orderList.forEach(it -> {
            GetOrderHistoryForEmployeeResponse orderResponse = GetOrderHistoryForEmployeeResponse.fromOrderBillEntity(it);
            resData.add(orderResponse);
        });
        return resData;
    }

    @Transactional
    @Override
    public void addNewOrderEvent(String id, OrderStatus orderStatus, String description, HttpServletRequest request)  {
        OrderBillEntity order = orderBillRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));

        String clientId = SecurityUtils.getCurrentUserId();
        UserEntity user = userRepository.findById(clientId).orElse(null);

        order.getOrderEventList().add(OrderEventEntity.builder()
                .orderStatus(orderStatus)
                .description(description)
                .orderBill(order)
                .isEmployee(user == null)
                .build()
        );

        OrderBillEntity updatedOrder = orderBillRepository.save(order);
        orderSearchService.upsertOrder(updatedOrder);
        TransactionEntity transaction = order.getTransaction();
        if(orderStatus == OrderStatus.CANCELED && transaction.getPaymentType() == PaymentType.BANKING_VNPAY) {
//            VNPayUtils.refund(request, transaction.getTimeCode(), order.getTotalPayment().toString(), transaction.getInvoiceCode(), "02");
            try {
                VNPayUtils.refund(request, transaction.getTimeCode(), order.getTotalPayment().toString(), transaction.getInvoiceCode(), "02");
            } catch (IOException e) {
                throw new CustomException(ErrorConstant.ERROR_REFUND);
            }
        }
    }

    @Override
    public List<GetShippingOrderQueueResponse> getShippingOrderQueueToday(OrderStatus orderStatus, int page, int size) {
        String employeeId = SecurityUtils.getCurrentUserId();

        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + employeeId));
        String branchId = employeeEntity.getBranch().getId();

        Pageable pageable = PageRequest.of(page - 1, size);
        List<OrderBillEntity> orderList = orderBillRepository.getShippingOrderQueueToday(orderStatus.name().toString(), branchId, OrderType.SHIPPING.name(), pageable).getContent();

        List<GetShippingOrderQueueResponse> orderListResponse = new ArrayList<>();
        orderList.forEach(it -> {
            GetShippingOrderQueueResponse orderResponse = GetShippingOrderQueueResponse.fromOrderBillEntity(it);
            orderListResponse.add(orderResponse);
        });
        return orderListResponse;
    }

    @Override
    public List<GetOnsiteOrderQueueResponse> getOnsiteOrderQueueToday(OrderStatus orderStatus, int page, int size) {
        String employeeId = SecurityUtils.getCurrentUserId();

        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + employeeId));
        String branchId = employeeEntity.getBranch().getId();


        Pageable pageable = PageRequest.of(page - 1, size);
        List<OrderBillEntity> orderList = orderBillRepository.getShippingOrderQueueToday(orderStatus.name().toString(), branchId, OrderType.ONSITE.name(), pageable).getContent();

        List<GetOnsiteOrderQueueResponse> orderListResponse = new ArrayList<>();
        orderList.forEach(it -> {
            GetOnsiteOrderQueueResponse orderResponse = GetOnsiteOrderQueueResponse.fromOrderBillEntity(it);
            orderListResponse.add(orderResponse);
        });
        return orderListResponse;
    }

    @Override
    public GetOrderListResponse getOrderListForAdmin(int page, int size, String key, OrderStatus status) {

        Pageable pageable = PageRequest.of(page - 1, size);
        String statusRegex = RegexUtils.generateFilterRegexString(status != null ? status.toString() : "");
        if (key != null) {
            Page<OrderIndex> orderPage = orderSearchService.searchOrderForAdmin(key, page, size, statusRegex);

            GetOrderListResponse resultPage = new GetOrderListResponse();
            resultPage.setTotalPage(orderPage.getTotalPages());
            resultPage.setOrderList(modelMapperService.mapList(orderPage.getContent(), GetOrderListResponse.Order.class));
            return resultPage;
        }
        Page<OrderBillEntity> orderBillPage = orderBillRepository.getOrderList(statusRegex, pageable);
        GetOrderListResponse dataResponse = new GetOrderListResponse();
        dataResponse.setTotalPage(orderBillPage.getTotalPages());

        List<GetOrderListResponse.Order> orderList = new ArrayList<>();
        orderBillPage.getContent().forEach(it -> {
            GetOrderListResponse.Order orderResponse = GetOrderListResponse.Order.fromOrderBillEntity(it);
            orderList.add(orderResponse);
        });
        dataResponse.setOrderList(orderList);
        return dataResponse;
    }

    @Override
    public GetOrderByIdResponse getOrderDetailsById(String id) {
        OrderBillEntity orderBill = orderBillRepository.findById(id).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));
        GetOrderByIdResponse order = GetOrderByIdResponse.fromOrderBillEntity(orderBill);
        return order;
    }

    @Override
    public List<GetAllOrderHistoryByUserIdResponse> getOrdersHistoryByUserId(String userId, OrderStatus orderStatus, int page, int size) {
        SecurityUtils.checkUserId(userId);
        Pageable pageable = PageRequest.of(page - 1, size);
        List<OrderBillEntity> orderList = orderBillRepository.getOrderListByUserIdAndStatus(orderStatus.name(), userId, pageable).getContent();
        List<GetAllOrderHistoryByUserIdResponse> response = new ArrayList<>();
        orderList.forEach(it -> {
            GetAllOrderHistoryByUserIdResponse order = GetAllOrderHistoryByUserIdResponse.fromOrderBillEntity(it);
            response.add(order);
        });

        return response;
    }

    @Override
    public List<GetOrderStatusLineResponse> getOrderEventLogById(String orderId) {
        List<OrderEventEntity> orderEventEntityList = orderEventRepository.findByOrderBill_IdOrderByCreatedAtDesc(orderId);
        List<GetOrderStatusLineResponse> eventList = new ArrayList<>();
        orderEventEntityList.forEach(it -> {
            GetOrderStatusLineResponse event = GetOrderStatusLineResponse.fromOrderEventEntity(it);
            eventList.add(event);
        });
        return eventList;
    }

    @Override
    public GetOrderQuantityByStatusResponse getOrderQuantityByStatusAtCurrentDate(OrderStatus orderStatus) {
        Date startDate = DateUtils.createDateTimeByToday(0, 0, 0, 0, 0);
        Date endDate = DateUtils.createDateTimeByToday(23, 59, 59, 999, 0);
        GetOrderQuantityByStatusResponse response = new GetOrderQuantityByStatusResponse();
        long current = orderBillRepository.countOrderInCurrentDateByStatus(orderStatus.name(), DateUtils.formatYYYYMMDD(new Date()));
        response.setOrderQuantity((int) current);

        Date startDatePrev = DateUtils.createDateTimeByToday(0, 0, 0, 0, -1);
        Date endDatePrev = DateUtils.createDateTimeByToday(23, 59, 59, 999, -1);

        long prev = orderBillRepository.countOrderInCurrentDateByStatus(orderStatus.name(), DateUtils.formatYYYYMMDD(startDatePrev));
        long difference = current - prev;
        response.setDifference((int) difference);
        return response;
    }
}
