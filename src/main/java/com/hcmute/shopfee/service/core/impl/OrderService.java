package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.constant.ShopfeeConstant;
import com.hcmute.shopfee.dto.common.ItemDetailDto;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.enums.param.OrderPhasesStatus;
import com.hcmute.shopfee.kafka.message.NewOrderMsgData;
import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.entity.sql.database.*;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.SubjectConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.UsageConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.reward.MoneyRewardReceivedEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.reward.ProductRewardReceivedEntity;
import com.hcmute.shopfee.entity.sql.database.order.*;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import com.hcmute.shopfee.entity.sql.database.payment.VNPayEntity;
import com.hcmute.shopfee.entity.sql.database.payment.ZaloPayEntity;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.entity.sql.database.product.SizeEntity;
import com.hcmute.shopfee.entity.sql.database.product.ToppingEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.kafka.message.OrderStatusMsgData;
import com.hcmute.shopfee.kafka.message.UserBlockedMsgData;
import com.hcmute.shopfee.kafka.publisher.EmployeeNotificationKafkaPublisher;
import com.hcmute.shopfee.kafka.publisher.MailerKafkaPublisher;
import com.hcmute.shopfee.kafka.publisher.UserNotificationKafkaPublisher;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.entity.elasticsearch.OrderIndex;
import com.hcmute.shopfee.module.goong.distancematrix.reponse.DistanceMatrixResponse;
import com.hcmute.shopfee.dto.common.vnpay.VNPayPaymentUrl;
import com.hcmute.shopfee.dto.common.zalopay.CreateOrderZaloPayResponse;
import com.hcmute.shopfee.repository.database.*;
import com.hcmute.shopfee.repository.database.coupon.CouponRepository;
import com.hcmute.shopfee.repository.database.coupon.condition.CombinationConditionRepository;
import com.hcmute.shopfee.repository.database.coupon_used.CouponUsedRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import com.hcmute.shopfee.repository.database.payment.VNPayRepository;
import com.hcmute.shopfee.repository.database.payment.ZaloPayRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import com.hcmute.shopfee.service.common.*;
import com.hcmute.shopfee.service.core.IOrderService;
import com.hcmute.shopfee.service.core.ITransactionService;
import com.hcmute.shopfee.service.elasticsearch.OrderEService;
import com.hcmute.shopfee.statemachine.OrderEvent;
import com.hcmute.shopfee.statemachine.OrderStateService;
import com.hcmute.shopfee.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.MessageFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService {
    private final CoinHistoryRepository coinHistoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderBillRepository orderBillRepository;
    private final ModelMapperService modelMapperService;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AddressRepository addressRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final OrderEService orderEService;
    private final EmployeeRepository employeeRepository;
    private final OrderEventRepository orderEventRepository;
    private final CouponRepository couponRepository;
    private final CouponUsedRepository couponUsedRepository;
    private final GoongService goongService;
    private final CombinationConditionRepository combinationConditionRepository;
    private final BranchService branchService;
    private final AhamoveService ahamoveService;
    private final VNPayService vnPayService;
    private final ZaloPayService zaloPayService;
    private final SchedulerService schedulerService;
    private final OrderStateService orderStateService;
    private final UserNotificationKafkaPublisher userNotificationKafkaPublisher;
    private final EmployeeNotificationKafkaPublisher employeeNotificationKafkaPublisher;
    private final MailerKafkaPublisher mailerKafkaPublisher;
    private final VNPayRepository vnPayRepository;
    private final ZaloPayRepository zaloPayRepository;

    @Autowired
    @Lazy
    private ITransactionService transactionService;

    private TransactionEntity buildTransaction(PaymentType paymentType, HttpServletRequest request, OrderBillEntity orderBill) {
        TransactionEntity transData = new TransactionEntity();

        if (paymentType == PaymentType.CASHING) {
            transData = new TransactionEntity();
            transData.setStatus(TransactionStatus.UNPAID);
            transData.setTotalPaid(0L);
            transData.setOrderBill(orderBill);
            transData.setPaymentType(PaymentType.CASHING);

//                    TransactionEntity.builder()
//                    .status(TransactionStatus.UNPAID)
//                    .totalPaid(0L)
//                    .orderBill(orderBill)
//                    .paymentType(PaymentType.CASHING).build();
        } else if (paymentType == PaymentType.VNPAY) {
            VNPayPaymentUrl paymentData = vnPayService.createUrlPayment(request, orderBill.getTotalPayment(), "Shipping Order Info");
            VNPayEntity vnPay = VNPayEntity.builder()
                    .invoiceCode(paymentData.getVnpTxnRef())
                    .timeCode(paymentData.getVnpCreateDate())
                    .paymentUrl(paymentData.getVnpUrl())
                    .build();

            vnPay.setStatus(TransactionStatus.UNPAID);
            vnPay.setTotalPaid(0L);
            vnPay.setOrderBill(orderBill);
            vnPay.setPaymentType(PaymentType.VNPAY);

            transData = vnPay;
//            transData = TransactionEntity.builder()
//                    .status(TransactionStatus.UNPAID)
//                    .paymentType(PaymentType.VNPAY)
//                    .vnPay(vnPay)
//                    .totalPaid(0L)
//                    .orderBill(orderBill)
//                    .build();
//            vnPay.setTransaction(transData);
        } else if (paymentType == PaymentType.ZALOPAY) {
            CreateOrderZaloPayResponse paymentData = zaloPayService.createOrderTransaction(orderBill.getTotalPayment());
            ZaloPayEntity zaloPay = ZaloPayEntity.builder()
//                    .transaction(transData)
                    .paymentUrl(paymentData.getOrderUrl())
                    .appTransactionId(paymentData.getInvoiceCode())
                    .build();


            zaloPay.setStatus(TransactionStatus.UNPAID);
            zaloPay.setTotalPaid(0L);
            zaloPay.setOrderBill(orderBill);
            zaloPay.setPaymentType(PaymentType.ZALOPAY);
            transData = zaloPay;
//            transData.setZaloPay(zaloPay);

//            transData = TransactionEntity.builder()
//                    .zaloPay(zaloPay)
//                    .status(TransactionStatus.UNPAID)
//                    .paymentType(PaymentType.ZALOPAY)
//                    .totalPaid(0L)
//                    .orderBill(orderBill)
//                    .build();
//            zaloPay.setTransaction(transData);
        }
//        orderBill.setTransaction(transData);
        return transData;
    }

    private long calculateTotalPriceItem(List<OrderItemDto> orderItemList, OrderBillEntity orderBill, String productCouponCode) {
        long totalPrice = 0;
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        int itemSize = orderItemList.size();
        List<String> productIdDiscountList = new ArrayList<>();
        long productDiscountValue = 0;
        MoneyRewardUnit productDiscountUnit = null;
        if (productCouponCode != null) {
            CouponEntity productCoupon = couponRepository.findByCodeAndStatusAndCouponTypeAndIsDeletedFalse(productCouponCode, CouponStatus.RELEASED, CouponType.PRODUCT)
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + productCouponCode));


            if (productCoupon.getRewardType() == CouponRewardType.MONEY) {
                productDiscountValue = productCoupon.getMoneyReward().getValue();
                productDiscountUnit = productCoupon.getMoneyReward().getUnit();

                List<SubjectConditionEntity> subjectConditionList = productCoupon.getConditionList().stream().filter(condition -> condition.getType() == ConditionType.SUBJECT)
                        .findFirst().orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.SupErrorCode.SERVER_ERROR, "Coupon condition is invalid")).getSubjectConditionList();
                productIdDiscountList = subjectConditionList.stream().map(SubjectConditionEntity::getObjectId).toList();
            }
        }


        for (int i = 0; i < itemSize; i++) {
            OrderItemDto orderItemDto = orderItemList.get(i);

            ProductEntity productInfo = productRepository.findByIdAndStatus(orderItemDto.getProductId(), ProductStatus.ACTIVE)
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND + orderItemDto.getProductId()));

            OrderItemEntity item = new OrderItemEntity(); // modelMapperService.mapClass(orderItemDto, OrderItemEntity.class);
            item.setProduct(productInfo);
            item.setName(productInfo.getName());
            item.setImageUrl(productInfo.getImage().getImageUrl());
            item.setThumbnailUrl(productInfo.getImage().getThumbnailUrl());

            item.setName(productInfo.getName());

            List<ToppingEntity> toppingList = productInfo.getToppingList();
            List<SizeEntity> sizeList = productInfo.getSizeList();

            List<ItemToppingEntity> itemsToppingList = new ArrayList<>();
            List<ItemDetailEntity> itemsDetailEntityList = new ArrayList<>();

            for (ItemDetailDto itemDetail : orderItemDto.getItemDetailList()) {
                long totalPriceToppings = 0;
                List<String> toppingNameList = itemDetail.getToppingNameList() != null ? itemDetail.getToppingNameList() : new ArrayList<>();

                // Khởi tạo và set các thuộc tính cơ bản
                ItemDetailEntity itemDetailEntity = new ItemDetailEntity();
                itemDetailEntity.setOrderItem(item);
                itemDetailEntity.setNote(itemDetail.getNote());
                itemDetailEntity.setQuantity(itemDetail.getQuantity());

                // set topping list cho item detail
                if (productInfo.getType() == ProductType.BEVERAGE) {
                    for (String toppingName : toppingNameList) {
                        ItemToppingEntity itemTopping = new ItemToppingEntity();
                        ToppingEntity toppingEntity = toppingList.stream()
                                .filter(topping -> toppingName.equals(topping.getName()))
                                .findFirst()
                                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.TOPPING_NOT_FOUND, ErrorConstant.NOT_FOUND + toppingName));
                        itemTopping.setName(toppingName);
                        itemTopping.setPrice(toppingEntity.getPrice());
                        itemTopping.setItemDetail(itemDetailEntity);
                        itemsToppingList.add(itemTopping);
                        totalPriceToppings += toppingEntity.getPrice();
                    }
                    itemDetailEntity.setItemToppingList(itemsToppingList);
                }

                // set size
                long productDiscount = 0;
                if (productInfo.getType() == ProductType.BEVERAGE) {
                    SizeEntity sizeItem = sizeList.stream()
                            .filter(it -> it.getSize() == itemDetail.getSize())
                            .findFirst().orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.SIZE_NOT_FOUND, ErrorConstant.NOT_FOUND + itemDetail.getSize()));

                    itemDetailEntity.setSize(itemDetail.getSize());
                    long productSizePrice = sizeItem.getPrice();
                    productDiscount = getProductDiscount(productIdDiscountList, orderItemDto.getProductId(), productDiscountUnit, productDiscountValue, productSizePrice);
                    itemDetailEntity.setPrice(sizeItem.getPrice());
                    itemDetailEntity.setProductDiscount(productDiscount);

                } else if (productInfo.getType() == ProductType.CAKE) {
                    itemDetailEntity.setPrice(productInfo.getPrice());
                    productDiscount = getProductDiscount(productIdDiscountList, orderItemDto.getProductId(), productDiscountUnit, productDiscountValue, productInfo.getPrice());
                    itemDetailEntity.setProductDiscount(productDiscount);
                }

                itemsDetailEntityList.add(itemDetailEntity);
                long productPriceFinal = itemDetailEntity.getPrice() - productDiscount > 0 ? itemDetailEntity.getPrice() - productDiscount : 0;
                totalPrice += (long) ((productPriceFinal + totalPriceToppings) * itemDetail.getQuantity());
            }

            item.setItemDetailList(itemsDetailEntityList);
            item.setOrderBill(orderBill);
            orderItemEntityList.add(item);
        }

        orderBill.setOrderItemList(orderItemEntityList);

        return totalPrice;
    }

    private long getProductDiscount(List<String> productIdDiscountList, String productId, MoneyRewardUnit productDiscountUnit, long productDiscountValue, long productPrice) {
        long productDiscount = 0;
        if (!productIdDiscountList.isEmpty() && productIdDiscountList.contains(productId)) {
            if (productDiscountUnit == MoneyRewardUnit.MONEY) {
                productDiscount = productDiscountValue;
            } else if (productDiscountUnit == MoneyRewardUnit.PERCENTAGE) {
                productDiscount = productPrice * productDiscountValue / 100;
            }
        }
        return productDiscount;
    }


    private void getCantCombinedCouponTypeList(String orderCouponCode, CouponType typeChecking, List<CouponType> sampleNot) {
        List<CouponType> sampleList = Arrays.asList(CouponType.PRODUCT, CouponType.ORDER, CouponType.SHIPPING);
        List<CouponType> couponTypeCombinedList = combinationConditionRepository.getCombinationConditionListByCouponCode(orderCouponCode);
        sampleList.forEach(couponType -> {
            if (!couponTypeCombinedList.contains(couponType) && typeChecking != couponType && !sampleNot.contains(couponType)) {
                sampleNot.add(couponType);
            }
        });
    }

    public void validateCoupon(String couponCode, List<OrderItemDto> orderItemList, long total, String userId) {
        CouponEntity coupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(couponCode, CouponStatus.RELEASED)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND + couponCode));

        Date currentTime = new Date();

        if (coupon.getStartDate().after(currentTime) || coupon.getExpirationDate().before(currentTime)) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Coupon is expired");
        }

        for (CouponConditionEntity condition : coupon.getConditionList()) {
            if (condition.getType() == ConditionType.MIN_PURCHASE) {
                if (total < condition.getMinPurchaseCondition().getValue()) {
                    // invalid
                    throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY, "Min purchase condition " + condition.getMinPurchaseCondition().getValue());
                }
            } else if (condition.getType() == ConditionType.USAGE) {
                for (UsageConditionEntity usageCondition : condition.getUsageConditionList()) {
                    if (usageCondition.getType() == UsageConditionType.QUANTITY) {
                        int usedCount = couponUsedRepository.getUsedCouponCount(coupon.getId());
                        if (usedCount >= usageCondition.getValue()) {
                            // invalid
                            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY, "Coupon quantity: " + usageCondition.getValue() + " - coupon used quantity: " + usedCount);
                        }
                    } else if (usageCondition.getType() == UsageConditionType.LIMIT_ONE_FOR_USER) {
                        CouponUsedEntity couponUsed = couponUsedRepository.getCouponUsedByUserIdAndCode(userId, coupon.getId()).orElse(null);
                        if (couponUsed != null) {
                            // invalid
                            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY, "Limit one per an user");
                        }
                    }
                }
            } else if (condition.getType() == ConditionType.SUBJECT) {

                List<SubjectConditionEntity> subjectConditionEntityList = condition.getSubjectConditionList();
                boolean isValid = false;
                for (SubjectConditionEntity subjectConditionEntity : subjectConditionEntityList) {
                    OrderItemDto item = orderItemList.stream().filter(it -> it.getProductId().equals(subjectConditionEntity.getObjectId())).findFirst()
                            .orElse(null);

                    if (item != null) {
                        int count = 0;
                        for (ItemDetailDto itemDetailDto : item.getItemDetailList()) {
                            count += itemDetailDto.getQuantity();
                        }
                        if (count >= subjectConditionEntity.getValue()) {
                            isValid = true;
                            break;
                        }
                    }

                }
                if(!isValid) {
                    throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, couponCode + " is invalid with subject condition");
                }
            }
        }
    }

    public void validateCouponForOrder(long totalItemPrice, List<OrderItemDto> itemList, String orderCouponCode, String shippingCouponCode, String productCouponCode) {
        String userId = SecurityUtils.getCurrentUserId();
        List<CouponType> cantCombinedCouponTypeList = new ArrayList<>();
        List<CouponType> couponTypeUsingList = new ArrayList<>();
        if (orderCouponCode != null) {
            couponTypeUsingList.add(CouponType.ORDER);
            getCantCombinedCouponTypeList(orderCouponCode, CouponType.ORDER, cantCombinedCouponTypeList);
            validateCoupon(orderCouponCode, itemList, totalItemPrice, userId);
        }
        if (shippingCouponCode != null) {
            couponTypeUsingList.add(CouponType.SHIPPING);
            getCantCombinedCouponTypeList(shippingCouponCode, CouponType.SHIPPING, cantCombinedCouponTypeList);
            validateCoupon(shippingCouponCode, itemList, totalItemPrice, userId);
        }
        if (productCouponCode != null) {
            couponTypeUsingList.add(CouponType.PRODUCT);
            validateCoupon(productCouponCode, itemList, totalItemPrice, userId);
            getCantCombinedCouponTypeList(productCouponCode, CouponType.PRODUCT, cantCombinedCouponTypeList);
        }

        cantCombinedCouponTypeList.forEach(it -> {
            if (couponTypeUsingList.contains(it)) {
                // invalid
                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Cant combine coupons");
            }
        });
    }


    private CouponUsedEntity createCouponUsedEntity(String couponCode) {
        CouponEntity coupon = couponRepository.findByCodeAndIsDeletedFalse(couponCode)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.COUPON_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + couponCode));

        CouponUsedEntity couponUsed = CouponUsedEntity.builder()
                .code(couponCode)
                .coupon(coupon)
                .type(CouponType.ORDER)
                .build();

        if (coupon.getRewardType() == CouponRewardType.MONEY) {
            MoneyRewardReceivedEntity moneyRewardReceived = MoneyRewardReceivedEntity.builder()
                    .unit(coupon.getMoneyReward().getUnit())
                    .value(coupon.getMoneyReward().getValue())
                    .couponUsed(couponUsed)
                    .build();
            couponUsed.setRewardType(CouponRewardType.MONEY);
            couponUsed.setMoneyRewardReceived(moneyRewardReceived);
        } else if (coupon.getRewardType() == CouponRewardType.PRODUCT_GIFT) {
            List<ProductRewardReceivedEntity> productRewardList = new ArrayList<>();
            coupon.getProductRewardList().forEach(productGift -> {
                ProductRewardReceivedEntity productRewardReceived = ProductRewardReceivedEntity.builder()
                        .product(productGift.getProduct())
                        .couponUsed(couponUsed)
                        .productSize(productGift.getProductSize())
                        .quantity(productGift.getQuantity())
                        .productName(productGift.getProductName())
                        .build();
                productRewardList.add(productRewardReceived);
            });
            couponUsed.setRewardType(CouponRewardType.PRODUCT_GIFT);
            couponUsed.setProductRewardReceivedList(productRewardList);
        }

        return couponUsed;
    }

    public long applyCouponForOrder(OrderBillEntity orderBill, String orderCouponCode, String shippingCouponCode, String productCouponCode) {
        long amountReduced = 0L;
        List<CouponUsedEntity> couponUsedList = new ArrayList<>();

        if (orderCouponCode != null) {
            CouponUsedEntity couponUsed = createCouponUsedEntity(orderCouponCode);
            couponUsed.setOrderBill(orderBill);
            couponUsedList.add(couponUsed);
            if (couponUsed.getRewardType() == CouponRewardType.MONEY) {
                if (couponUsed.getMoneyRewardReceived().getUnit() == MoneyRewardUnit.MONEY) {
                    orderBill.setOrderDiscount(Long.valueOf(couponUsed.getMoneyRewardReceived().getValue()));
                    amountReduced += orderBill.getOrderDiscount();
                } else if (couponUsed.getMoneyRewardReceived().getUnit() == MoneyRewardUnit.PERCENTAGE) {
                    orderBill.setOrderDiscount(orderBill.getTotalItemPrice() * couponUsed.getMoneyRewardReceived().getValue() / 100);
                    amountReduced += orderBill.getOrderDiscount();
                }
            }
        }
        if (shippingCouponCode != null) {
            CouponUsedEntity couponUsed = createCouponUsedEntity(shippingCouponCode);
            couponUsed.setOrderBill(orderBill);
            couponUsedList.add(couponUsed);
            if (couponUsed.getRewardType() == CouponRewardType.MONEY) {
                if (couponUsed.getMoneyRewardReceived().getUnit() == MoneyRewardUnit.MONEY) {
                    long shippingFeeDiscount = couponUsed.getMoneyRewardReceived().getValue();
                    orderBill.setShippingDiscount(orderBill.getShippingFee() <= shippingFeeDiscount ? orderBill.getShippingFee() : shippingFeeDiscount);
                    amountReduced += orderBill.getShippingDiscount();
                } else if (couponUsed.getMoneyRewardReceived().getUnit() == MoneyRewardUnit.PERCENTAGE) {
                    orderBill.setShippingDiscount(orderBill.getShippingFee() * couponUsed.getMoneyRewardReceived().getValue() / 100);
                    amountReduced += orderBill.getShippingDiscount();
                }
            }
        }
        if (productCouponCode != null) {
            CouponUsedEntity couponUsed = createCouponUsedEntity(productCouponCode);
            couponUsed.setOrderBill(orderBill);
            couponUsedList.add(couponUsed);
        }
        orderBill.setCouponUsedList(couponUsedList);
        return amountReduced;
    }

    @Transactional
    @Override
    public CreateOrderResponse createShippingOrder(CreateShippingOrderRequest body, HttpServletRequest request) {
        SecurityUtils.checkUserId(body.getUserId());

        if ((body.getTotal() < 10000 && body.getPaymentType() == PaymentType.VNPAY)
                || (body.getTotal() < 2000 && body.getPaymentType() == PaymentType.ZALOPAY)
        ) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, ErrorConstant.VNPAY_MONEY_INVALID);
        }

        long totalPayment = 0L;
        String userId = SecurityUtils.getCurrentUserId();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.USER_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + userId));
        long deductCoin = body.getCoin() != null ? body.getCoin() : 0L;
        long userCoin = coinHistoryRepository.getCoinOfUser(userId);
        if (userCoin < deductCoin) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "User's coin count is less than the amount posted");
        }


        OrderBillEntity orderBill = modelMapperService.mapClass(body, OrderBillEntity.class);
        orderBill.setUser(user);
        orderBill.setOrderType(OrderType.SHIPPING);

        long totalItemPrice = calculateTotalPriceItem(body.getItemList(), orderBill, body.getProductCouponCode());
        orderBill.setTotalItemPrice(totalItemPrice);
        totalPayment += totalItemPrice;

        // set địa chỉ giao hàng
        AddressEntity address = addressRepository.findById(body.getAddressId())
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ADDRESS_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + body.getAddressId()));
        ReceiverInformationEntity shippingInformation = new ReceiverInformationEntity();
        shippingInformation.fromAddressEntity(address);

        shippingInformation.setOrderBill(orderBill);
        orderBill.setReceiverInformation(shippingInformation);

        // set sự kiện đơn hàng
        List<OrderEventEntity> orderEventList = new ArrayList<>();
        orderEventList.add(OrderEventEntity.builder()
                .orderStatus(OrderStatus.CREATED)
                .description(OrderStatus.CREATED.getResultDescription())
                .orderBill(orderBill)
                .actor(ActorType.USER)
                .build());
        orderBill.setOrderEventList(orderEventList);

        // set chi nhánh xử lý đơn
        Time currentTime = DateUtils.getCurrentTime();
        BranchEntity branch = branchService.getNearestBranchAndValidateTime(address.getLatitude(), address.getLongitude(), currentTime);
        orderBill.setBranch(branch);

        // set tổng hóa đơn và phí ship
        orderBill.setShippingFee(body.getShippingFee());


        // xử lý coupon
        validateCouponForOrder(totalItemPrice, body.getItemList(), body.getOrderCouponCode(), body.getShippingCouponCode(), body.getProductCouponCode());
        long amountReduced = applyCouponForOrder(orderBill, body.getOrderCouponCode(), body.getShippingCouponCode(), body.getProductCouponCode());

        totalPayment -= amountReduced;

        // tính phí vận chuyển
        totalPayment += body.getShippingFee();

        // kiểm tra số coin kh được > tổng tiền trước khi apply coin
        if (deductCoin > totalPayment) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "The number of coins used cannot be greater than the total bill");
        }

        CoinHistoryEntity coinHistory = CoinHistoryEntity.builder()
                .actor(ActorType.USER)
                .user(user)
                .build();

        orderBill.setCoin(deductCoin);
        if (deductCoin != 0) {
            coinHistory.setCoin(-deductCoin);
            totalPayment -= deductCoin;
        }

        if (totalPayment != body.getTotal()) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Total order is invalid");
        }
        orderBill.setTotalPayment(totalPayment);

        // set giao dịch
        TransactionEntity transaction = buildTransaction(body.getPaymentType(), request, orderBill);
        orderBill.setTransaction(transaction);

        orderBill = orderBillRepository.save(orderBill);

        orderEService.upsertOrder(orderBill);

        if (deductCoin != 0) {
            // save coin history
            coinHistory.setDescription(ShopfeeConstant.DEDUCT_COIN_TO_PAY + orderBill.getId());
            coinHistoryRepository.save(coinHistory);
        }
        CreateOrderResponse resData = CreateOrderResponse.builder()
                .orderId(orderBill.getId())
                .branchId(orderBill.getBranch().getId())
                .transactionId(transaction.getId())
                .build();

        // set schedule for payment, transaction
        String paymentUrl = transaction.getPaymentType() == PaymentType.ZALOPAY ? ((ZaloPayEntity) transaction).getPaymentUrl() :
                transaction.getPaymentType() == PaymentType.VNPAY ? ((VNPayEntity) transaction).getPaymentUrl() : null;
        if (paymentUrl != null && totalPayment > 0) {
            resData.setPaymentUrl(paymentUrl);

            schedulerService.setScheduleTransaction(transaction);
        }
        schedulerService.setAutoCancelOrder(orderBill);

        if (transaction.getPaymentType() == PaymentType.CASHING) {
            NewOrderMsgData notificationDto = new NewOrderMsgData(orderBill.getBranch().getId(), String.format(ShopfeeConstant.NEW_ORDER_MSG, ShopfeeConstant.SHIPPING_ORDER_TITLE_MSG, orderBill.getId()));
            userNotificationKafkaPublisher.sendNotificationToBranch(notificationDto);
        }

        return resData;
    }

    @Transactional
    @Override
    public CreateOrderResponse createOnsiteOrder(CreateOnsiteOrderRequest body, HttpServletRequest request) {
        SecurityUtils.checkUserId(body.getUserId());

        if ((body.getTotal() < 10000 && body.getPaymentType() == PaymentType.VNPAY)
                || (body.getTotal() < 2000 && body.getPaymentType() == PaymentType.ZALOPAY)) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, ErrorConstant.VNPAY_MONEY_INVALID);
        }

        long totalPayment = 0L;
        String userId = SecurityUtils.getCurrentUserId();
        UserEntity user = userRepository.findById(body.getUserId())
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.USER_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + userId));
        long deductCoin = body.getCoin() != null ? body.getCoin() : 0L;

        long userCoin = coinHistoryRepository.getCoinOfUser(userId);
        if (userCoin < deductCoin) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "User's coin count is less than the amount posted");
        }

        OrderBillEntity orderBill = modelMapperService.mapClass(body, OrderBillEntity.class);
        orderBill.setUser(user);
        orderBill.setOrderType(OrderType.ONSITE);

        long totalPriceItem = calculateTotalPriceItem(body.getItemList(), orderBill, body.getProductCouponCode());
        orderBill.setTotalItemPrice(totalPriceItem);
        totalPayment += totalPriceItem;

        validateCouponForOrder(totalPriceItem, body.getItemList(), body.getOrderCouponCode(), null, body.getProductCouponCode());
        long amountReduced = applyCouponForOrder(orderBill, body.getOrderCouponCode(), null, body.getProductCouponCode());

        totalPayment -= amountReduced;

        // kiểm tra số coin kh được > tổng tiền trước khi apply coin
        if (deductCoin > totalPayment) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "The number of coins used cannot be greater than the total bill");
        }
        CoinHistoryEntity coinHistory = CoinHistoryEntity.builder()
                .actor(ActorType.USER)
                .user(user)
                .build();

        orderBill.setCoin(deductCoin);
        if (deductCoin != 0) {
            totalPayment -= body.getCoin();
            coinHistory.setCoin(-body.getCoin());
        }


        if (totalPayment != body.getTotal()) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Total order is invalid");
        }

        // set total payment
        orderBill.setTotalPayment(totalPayment);

        // set giao dịch
        TransactionEntity transaction = buildTransaction(body.getPaymentType(), request, orderBill);
        orderBill.setTransaction(transaction);

        // set sự kiện đơn hàng
        List<OrderEventEntity> orderEventList = new ArrayList<>();
        orderEventList.add(OrderEventEntity.builder()
                .orderStatus(OrderStatus.CREATED)
                .description(OrderStatus.CREATED.getResultDescription())
                .actor(ActorType.USER)
                .orderBill(orderBill)
                .build());
        orderBill.setOrderEventList(orderEventList);

        // set chi nhánh đặt hàng
        BranchEntity branch = branchRepository.findById(body.getBranchId())
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BRANCH_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + body.getBranchId()));
        orderBill.setBranch(branch);
        Date receiveTime = body.getReceiveTime();
        if(receiveTime.before(new Date()) || !DateUtils.isInRangeTime(receiveTime.toInstant(), branch.getOpenTime(), branch.getCloseTime())) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Pick-up time is invalid");
        }

        // set thong tin nhan hang
        orderBill.setReceiverInformation(ReceiverInformationEntity.builder()
                .phoneNumber(body.getPhoneNumber())
                .receiveTime(body.getReceiveTime())
                .recipientName(body.getRecipientName())
                .orderBill(orderBill)
                .build());


        orderBill = orderBillRepository.save(orderBill);
        orderEService.upsertOrder(orderBill);

        // save coin history
        if (deductCoin != 0) {
            coinHistory.setDescription(ShopfeeConstant.DEDUCT_COIN_TO_PAY + orderBill.getId());
            coinHistoryRepository.save(coinHistory);
        }

        transaction = orderBill.getTransaction();

        CreateOrderResponse resData = CreateOrderResponse.builder()
                .orderId(orderBill.getId())
                .transactionId(transaction.getId())
                .build();


        // set schedule for payment
        String paymentUrl = transaction.getPaymentType() == PaymentType.ZALOPAY ? ((ZaloPayEntity) transaction).getPaymentUrl() :
                transaction.getPaymentType() == PaymentType.VNPAY ? ((VNPayEntity) transaction).getPaymentUrl() : null;
        if (paymentUrl != null && totalPayment > 0) {
            resData.setPaymentUrl(paymentUrl);
            schedulerService.setScheduleTransaction(transaction);
        }
        schedulerService.setAutoCancelOrder(orderBill);

        if (transaction.getPaymentType() == PaymentType.CASHING) {
            NewOrderMsgData notificationDto = new NewOrderMsgData(orderBill.getBranch().getId(), String.format(ShopfeeConstant.NEW_ORDER_MSG, ShopfeeConstant.ONSITE_ORDER_TITLE_MSG, orderBill.getId()));
            userNotificationKafkaPublisher.sendNotificationToBranch(notificationDto);
        }
        return resData;
    }

    @Override
    public GetOrderHistoryForEmployeeResponse getOrderHistoryPageForEmployee(OrderStatus orderStatus, int page, int size, String key) {
        String statusRegex = RegexUtils.generateFilterRegexString(orderStatus != null ? orderStatus.toString() : "");
        Pageable pageable = PageRequest.of(page - 1, size);
        GetOrderHistoryForEmployeeResponse data = new GetOrderHistoryForEmployeeResponse();

        if (!key.isBlank()) {
            Page<OrderIndex> orderPage = orderEService.searchOrderForAdmin(key, page, size, statusRegex);
            data.setTotalPage(orderPage.getTotalPages());
            data.setOrderList(GetOrderHistoryForEmployeeResponse.fromOrderIndexList(orderPage.getContent()));
            return data;
        }

        Page<OrderBillEntity> orderList = orderBillRepository.getOrderBillByLastStatus(orderStatus.name(), pageable);
        data.setTotalPage(orderList.getTotalPages());
        data.setOrderList(GetOrderHistoryForEmployeeResponse.fromOrderBillEntityList(orderList.getContent()));
        return data;
    }

    @Transactional
    @Override
    public void insertOrderEventByEmployee(String orderId, UpdateOrderStatusRequest body, HttpServletRequest request) {
        String employeeId = SecurityUtils.getCurrentUserId();
        List<OrderEvent> validOrderEvent = Arrays.asList(OrderEvent.EMPLOYEE_ORDER_REFUSE, OrderEvent.ACCEPT_ORDER,
                OrderEvent.REFUSE_ORDER_CANCELLATION, OrderEvent.ACCEPT_ORDER_CANCELLATION, OrderEvent.PREPARED,
                OrderEvent.START_SHIPPING, OrderEvent.BOOM, OrderEvent.FULFILL);

        if (!validOrderEvent.contains(body.getEvent())) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "Order event is not valid");
        }

        OrderBillEntity orderBill = orderBillRepository.findById(orderId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ORDER_BILL_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + orderId));
        UserEntity user = orderBill.getUser();
        boolean rs = orderStateService.sendMonoEvent(orderId, body.getNote(), body.getEvent());
        if (!rs) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY);
        }

        // xu ly hoan tien / xu khi nhan vien ACCEPT_ORDER_CANCELLATION
        if (body.getEvent() == OrderEvent.ACCEPT_ORDER_CANCELLATION) {
            transactionService.refundOrder(orderBill, true, true);
        }
        // cap nhat transaction cashing khi order thanh cong === con banking thi dc cap nhat ngay sau khi CREATED
        else if (body.getEvent() == OrderEvent.FULFILL) {
            TransactionEntity trans = orderBill.getTransaction();
            if (trans.getPaymentType() == PaymentType.CASHING) {
                long totalPaid = orderBill.getTotalItemPrice();
                trans.setStatus(TransactionStatus.PAID);
                trans.setTotalPaid(totalPaid);
                transactionRepository.save(trans);
            }
        }
        else if(body.getEvent() == OrderEvent.PREPARED) {
            if(orderBill.getOrderType() == OrderType.ONSITE) {
                schedulerService.setAutoBoomWhenNoOneReceiveOrder(orderBill.getId(), orderBill.getReceiverInformation().getReceiveTime());
            }
        }
        else if(body.getEvent() == OrderEvent.BOOM) {
            int boomCount = orderEventRepository.getCountOrderBillWithStatus(user.getId(), OrderStatus.NOT_RECEIVED.name());
            if(boomCount + 1 >= ShopfeeConstant.ORDER_BOOM_COUNT_LIMIT) {
                user.setStatus(UserStatus.BLOCKED);
                userRepository.save(user);
                UserBlockedMsgData msg = new UserBlockedMsgData();
                msg.setEmail(user.getEmail());
                msg.setUserName(user.getFullName());
                mailerKafkaPublisher.sendBlockedStatus(msg);
            }
        }

        OrderStatusMsgData message = OrderStatusMsgData.builder()
                .clientId(orderBill.getUser().getId())
                .title(ShopfeeConstant.USER_NOTI_TITLE_MSG)
                .body(MessageFormat.format(body.getEvent().getNotificationMsg(), orderId, employeeId))
                .build();

        employeeNotificationKafkaPublisher.sendNotificationToUserId(message);

        OrderBillEntity updatedOrder = orderBillRepository.save(orderBill);
        orderEService.upsertOrder(updatedOrder);
    }

    @Transactional
    @Override
    public void createCancellationRequest(CreateCancellationDemandRequest body, String orderId) {
        OrderBillEntity orderBill = orderBillRepository.findById(orderId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ORDER_BILL_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + orderId));

        UserEntity user = orderBill.getUser();
        SecurityUtils.checkUserId(user.getId());

        boolean rs = orderStateService.sendMonoEvent(orderId, body.getNote(), OrderEvent.REQUEST_CANCEL);
        if (!rs) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY);
        }

        orderBill = orderBillRepository.save(orderBill);
        orderEService.upsertOrder(orderBill);


        NewOrderMsgData notificationDto = new NewOrderMsgData(orderBill.getBranch().getId(), "A new cancellation request", "From customer " + user.getId());
        userNotificationKafkaPublisher.sendNotificationToBranch(notificationDto);
    }

    @Override
    @Transactional
    public void cancelOrder(String orderId, CancelOrderBillRequest body) {
        OrderBillEntity orderBill = orderBillRepository.findById(orderId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ORDER_BILL_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + orderId));

        UserEntity user = orderBill.getUser();
        SecurityUtils.checkUserId(user.getId());
        boolean rs = orderStateService.sendMonoEvent(orderId, body.getNote(), OrderEvent.USER_REFUSE);
        if (!rs) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY);
        }
        // refund tien + xu khi user cancel
        transactionService.refundOrder(orderBill, true, true);

        OrderBillEntity updatedOrder = orderBillRepository.save(orderBill);
        orderEService.upsertOrder(updatedOrder);

        NewOrderMsgData notificationDto = new NewOrderMsgData(orderBill.getBranch().getId(), "A new cancellation request", "From customer " + user.getId());
        userNotificationKafkaPublisher.sendNotificationToBranch(notificationDto);
    }

    @Override
    public GetOrderQueueResponse getShippingOrderQueueToday(OrderStatus orderStatus, int page, int size) {
        String employeeId = SecurityUtils.getCurrentUserId();
        GetOrderQueueResponse data = new GetOrderQueueResponse();
        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + employeeId));
        String branchId = employeeEntity.getBranch().getId();

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<OrderBillEntity> orderPage = orderBillRepository.getOrderQueueToday(orderStatus.name(), branchId, OrderType.SHIPPING.name(), pageable);

        data.setTotalPage(orderPage.getTotalPages());
        data.setOrderList(GetOrderQueueResponse.fromOrderBillEntityList(orderPage.getContent()));

        return data;
    }

    @Override
    public GetOrderQueueResponse getOnsiteOrderQueueToday(OrderStatus orderStatus, int page, int size) {
        String employeeId = SecurityUtils.getCurrentUserId();

        GetOrderQueueResponse data = new GetOrderQueueResponse();
        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + employeeId));
        String branchId = employeeEntity.getBranch().getId();


        Pageable pageable = PageRequest.of(page - 1, size);
        Page<OrderBillEntity> orderPage = orderBillRepository.getOrderQueueToday(orderStatus.name().toString(), branchId, OrderType.ONSITE.name(), pageable);

        data.setTotalPage(orderPage.getTotalPages());
        data.setOrderList(GetOrderQueueResponse.fromOrderBillEntityList(orderPage.getContent()));

        return data;
    }

    @Override
    public GetOrderListResponse getOrderListForAdmin(int page, int size, String key, OrderStatus status) {

        Pageable pageable = PageRequest.of(page - 1, size);
        String statusRegex = RegexUtils.generateFilterRegexString(status != null ? status.toString() : "");

        if (key != null) {
            Page<OrderIndex> orderPage = orderEService.searchOrderForAdmin(key, page, size, statusRegex);
            GetOrderListResponse resultPage = new GetOrderListResponse();
            resultPage.setTotalPage(orderPage.getTotalPages());
            resultPage.setOrderList(modelMapperService.mapList(orderPage.getContent(), GetOrderListResponse.Order.class));
            return resultPage;
        }

        Page<OrderBillEntity> orderBillPage = orderBillRepository.getOrderListForAdmin(status == null ? "" : status.name(), pageable);
        GetOrderListResponse dataResponse = new GetOrderListResponse();
        dataResponse.setTotalPage(orderBillPage.getTotalPages());

        dataResponse.setOrderList(GetOrderListResponse.fromOrderBillEntityList(orderBillPage.getContent()));
        return dataResponse;
    }

    @Override
    public GetOrderByIdResponse getOrderDetailsById(String orderId) {
        OrderBillEntity orderBill = orderBillRepository.findById(orderId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ORDER_BILL_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + orderId));

        OrderEventEntity orderEvent = orderBill.getOrderEventList().stream().filter(event -> event.getOrderStatus() == OrderStatus.ACCEPTED).findFirst().orElse(null);
        EmployeeEntity employeeEntity = employeeRepository.findById(orderEvent != null ? orderEvent.getCreatedBy() : "")
                .orElse(null);
        return GetOrderByIdResponse.fromOrderBillEntityAndEmployee(orderBill, employeeEntity);
    }

    @Override
    public List<GetOrderItemAndReviewResponse> getOrderItemAndReviewByOrderBillId(String orderBillId) {
        List<OrderItemEntity> orderItemEntityList = orderItemRepository.findByOrderBill_Id(orderBillId);
        return GetOrderItemAndReviewResponse.fromOrderItemEntityList(orderItemEntityList);
    }

    @Override
    public GetShippingFeeResponse getShippingFee(Double lat, Double lng) {
        GetShippingFeeResponse data = new GetShippingFeeResponse();
        List<BranchEntity> branchEntityList = branchRepository.findByStatus(BranchStatus.ACTIVE);
        String clientCoordinates = lat + "," + lng;
        List<String> destinationCoordinatesList = LocationUtils.getCoordinatesListFromBranchList(branchEntityList);
        List<DistanceMatrixResponse.Row.Element.Distance> distanceList = goongService.getDistanceFromClientToBranches(clientCoordinates, destinationCoordinatesList, "bike");
        int branchSize = branchEntityList.size();


        Time currentTime = DateUtils.getCurrentTime();
        BranchEntity branchEntity = branchService.getNearestBranchAndValidateTime(lat, lng, currentTime);

        int shippingFee = ahamoveService.getShippingFee(lat, lng, branchEntity.getLatitude(), branchEntity.getLongitude());
        data.setShippingFee(shippingFee);
        return data;
    }

    @Override
    public List<GetAllOrderHistoryByUserIdResponse> getOrdersHistoryByUserId(String userId, OrderPhasesStatus orderPhasesStatus, int page, int size) {
        SecurityUtils.checkUserId(userId);
        Pageable pageable = PageRequest.of(page - 1, size);
        List<String> orderStatusList = new ArrayList<>();
        if (orderPhasesStatus == OrderPhasesStatus.WAITING) {
            orderStatusList.add(OrderStatus.CREATED.name());
        } else if (orderPhasesStatus == OrderPhasesStatus.IN_PROCESS) {
            List<String> statusesToAdd = Arrays.asList(OrderStatus.ACCEPTED.name(), OrderStatus.IN_DELIVERY.name(),
                    OrderStatus.PENDING_PICK_UP.name(),
                    OrderStatus.CANCELLATION_REQUEST.name(), OrderStatus.CANCELLATION_REQUEST_ACCEPTED.name(),
                    OrderStatus.CANCELLATION_REQUEST_REFUSED.name());
            orderStatusList.addAll(statusesToAdd);
        } else if (orderPhasesStatus == OrderPhasesStatus.SUCCEED) {
            orderStatusList.add(OrderStatus.SUCCEED.name());
        } else if (orderPhasesStatus == OrderPhasesStatus.CANCELED) {
            orderStatusList.add(OrderStatus.CANCELED.name());
        } else if (orderPhasesStatus == OrderPhasesStatus.NOT_RECEIVED) {
            orderStatusList.add(OrderStatus.NOT_RECEIVED.name());
        }
//        orderStatusList = List.of("sadsd");
        List<OrderBillEntity> orderList = orderBillRepository.getOrderListByUserIdAndStatus(orderStatusList, userId, pageable).getContent();

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
}
