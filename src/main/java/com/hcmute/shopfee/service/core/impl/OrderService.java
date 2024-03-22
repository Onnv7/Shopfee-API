package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.ItemDetailDto;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.entity.database.*;
import com.hcmute.shopfee.entity.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.database.coupon.CouponEntity;
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
import com.hcmute.shopfee.module.goong.distancematrix.reponse.DistanceMatrixResponse;
import com.hcmute.shopfee.module.vnpay.transaction.dto.PreTransactionInfo;
import com.hcmute.shopfee.module.zalopay.order.dto.response.CreateOrderZaloPayResponse;
import com.hcmute.shopfee.repository.database.*;
import com.hcmute.shopfee.repository.database.coupon.CouponRepository;
import com.hcmute.shopfee.repository.database.coupon.condition.CombinationConditionRepository;
import com.hcmute.shopfee.repository.database.coupon_used.CouponUsedRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.schedule.job.AcceptOrderJob;
import com.hcmute.shopfee.schedule.job.TransactionQueryJob;
import com.hcmute.shopfee.service.common.*;
import com.hcmute.shopfee.service.core.IOrderService;
import com.hcmute.shopfee.service.elasticsearch.OrderSearchService;
import com.hcmute.shopfee.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.hcmute.shopfee.constant.ErrorConstant.USER_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
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
    private final GoongService goongService;
    private final CombinationConditionRepository combinationConditionRepository;
    private final BranchService branchService;
    private final CancellationDemandRepository cancellationDemandRepository;
    private final Scheduler scheduler;
    private final AhamoveService ahamoveService;
    private final VNPayService vnPayService;
    private final ZaloPayService zaloPayService;
    private final SchedulerService schedulerService;


    private void buildTransaction(PaymentType paymentType, HttpServletRequest request, OrderBillEntity orderBill) {
        TransactionEntity transData = new TransactionEntity();

        if (paymentType == PaymentType.CASHING) {
            transData = TransactionEntity.builder()
                    .status(PaymentStatus.UNPAID)
                    .totalPaid(0L)
                    .paymentType(PaymentType.CASHING).build();
        } else if (paymentType == PaymentType.VNPAY) {
            PreTransactionInfo paymentData = vnPayService.createUrlPayment(request, orderBill.getTotalPayment(), "Shipping Order Info");
            transData = TransactionEntity.builder()
                    .invoiceCode(paymentData.getVnpTxnRef())
                    .timeCode(paymentData.getVnpCreateDate())
                    .status(PaymentStatus.UNPAID)
                    .paymentType(PaymentType.VNPAY)
                    .totalPaid(0L)
                    .paymentUrl(paymentData.getVnpUrl())
                    .build();
        } else if (paymentType == PaymentType.ZALOPAY) {
            CreateOrderZaloPayResponse paymentData = zaloPayService.createOrder(orderBill.getTotalPayment(), orderBill.getId());
            transData = TransactionEntity.builder()
                    .invoiceCode(paymentData.getInvoiceCode())
                    .status(PaymentStatus.UNPAID)
                    .paymentUrl(paymentData.getOrderUrl())
                    .paymentType(PaymentType.ZALOPAY)
                    .totalPaid(0L)
                    .build();
        }
        orderBill.setTransaction(transData);
    }

    private long calculateOrderBill(List<OrderItemDto> orderItemList, OrderBillEntity orderBill) {
        long totalPrice = 0;
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        int itemSize = orderItemList.size();
        for (int i = 0; i < itemSize; i++) {
            OrderItemDto orderItemDto = orderItemList.get(i);

            ProductEntity productInfo = productRepository.findByIdAndStatus(orderItemDto.getProductId(), ProductStatus.AVAILABLE)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.PRODUCT_ID_NOT_FOUND + orderItemDto.getProductId()));

            OrderItemEntity item = new OrderItemEntity(); // modelMapperService.mapClass(orderItemDto, OrderItemEntity.class);
            item.setProduct(productInfo);
            item.setName(productInfo.getName());


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
                itemDetailEntity.setSize(itemDetail.getSize());

                // set topping list cho item detail
                for (String toppingName : toppingNameList) {
                    ItemToppingEntity itemTopping = new ItemToppingEntity();
                    ToppingEntity toppingEntity = toppingList.stream()
                            .filter(topping -> toppingName.equals(topping.getName()))
                            .findFirst()
                            .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Topping with name " + toppingName));
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
                        .findFirst().orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Product's size with id " + itemDetail.getSize()));
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


    private void getCantCombinedCouponTypeList(String orderCouponCode, CouponType typeChecking, List<CouponType> sampleNot) {
        List<CouponType> sample = Arrays.asList(CouponType.PRODUCT, CouponType.ORDER, CouponType.SHIPPING);
        List<CouponType> couponTypeCombinedList = combinationConditionRepository.getCombinationConditionByCouponCode(orderCouponCode);
        sample.forEach(couponType -> {
            if (!couponTypeCombinedList.contains(couponType) && typeChecking != couponType && !sampleNot.contains(couponType)) {
                sampleNot.add(couponType);
            }
        });
    }

    public void validateCoupon(String couponCode, List<OrderItemDto> orderItemList, long total, String userId) {
        CouponEntity coupon = couponRepository.findByCodeAndStatusAndIsDeletedFalse(couponCode, CouponStatus.RELEASED)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Coupon with code " + couponCode));

        Date currentTime = new Date();

        if (coupon.getStartDate().after(currentTime) || coupon.getExpirationDate().before(currentTime)) {
            throw new CustomException(ErrorConstant.COUPON_INVALID, "Coupon is expired");
        }

        for (CouponConditionEntity condition : coupon.getConditionList()) {
            if (condition.getType() == ConditionType.MIN_PURCHASE) {
                if (total < condition.getMinPurchaseCondition().getValue()) {
                    // invalid
                    throw new CustomException(ErrorConstant.COUPON_INVALID, "Min purchase condition " + condition.getMinPurchaseCondition().getValue());
                }
            } else if (condition.getType() == ConditionType.USAGE) {
                for (UsageConditionEntity usageCondition : condition.getUsageConditionList()) {
                    if (usageCondition.getType() == UsageConditionType.QUANTITY) {
                        int usedCount = couponUsedRepository.getUsedCouponCount(coupon.getId());
                        if (usedCount >= usageCondition.getValue()) {
                            // invalid
                            throw new CustomException(ErrorConstant.COUPON_INVALID, "Coupon quantity: " + usageCondition.getValue() + " - coupon used quantity: " + usedCount);
                        }
                    } else if (usageCondition.getType() == UsageConditionType.LIMIT_ONE_FOR_USER) {
                        CouponUsedEntity couponUsed = couponUsedRepository.getCouponUsedByUserIdAndCode(userId, coupon.getId()).orElse(null);
                        if (couponUsed != null) {
                            // invalid
                            throw new CustomException(ErrorConstant.COUPON_INVALID, "Limit one per an user");
                        }
                    }
                }
            } else if (condition.getType() == ConditionType.TARGET_OBJECT) {

                List<SubjectConditionEntity> subjectConditionEntityList = condition.getSubjectConditionList();
                for (SubjectConditionEntity subjectConditionEntity : subjectConditionEntityList) {
                    OrderItemDto item = orderItemList.stream().filter(it -> it.getProductId().equals(subjectConditionEntity.getObjectId())).findFirst().orElse(null);
                    if (item == null) {
                        // invalid
                        throw new CustomException(ErrorConstant.COUPON_INVALID, "Not found subject " + subjectConditionEntity.getObjectId());
                    } else {
                        int count = 0;
                        for (ItemDetailDto itemDetailDto : item.getItemDetailList()) {
                            count += itemDetailDto.getQuantity();
                        }
                        if (count < subjectConditionEntity.getValue()) {
                            // invalid
                            throw new CustomException(ErrorConstant.COUPON_INVALID, "Subject " + subjectConditionEntity.getObjectId() + " quantity " + subjectConditionEntity.getValue());
                        }
                    }
                }
            }
        }
    }

    public void validateCouponForOrder(long totalItemPrice, List<OrderItemDto> itemList, String orderCouponCode, String shippingCouponCode, String productCouponCode) {
        String userId = SecurityUtils.getCurrentUserId();
        List<String> couponCodeList = new ArrayList<>();
        List<CouponType> cantCombinedCouponTypeList = new ArrayList<>();
        List<CouponType> couponTypeUsingList = new ArrayList<>();
        if (orderCouponCode != null) {
            couponCodeList.add(orderCouponCode);
            couponTypeUsingList.add(CouponType.ORDER);
            getCantCombinedCouponTypeList(orderCouponCode, CouponType.ORDER, cantCombinedCouponTypeList);
            validateCoupon(orderCouponCode, itemList, totalItemPrice, userId);
        }
        if (shippingCouponCode != null) {
            couponCodeList.add(shippingCouponCode);
            couponTypeUsingList.add(CouponType.SHIPPING);
            getCantCombinedCouponTypeList(shippingCouponCode, CouponType.SHIPPING, cantCombinedCouponTypeList);
            validateCoupon(shippingCouponCode, itemList, totalItemPrice, userId);
        }
        if (productCouponCode != null) {
            couponCodeList.add(productCouponCode);
            couponTypeUsingList.add(CouponType.PRODUCT);
            validateCoupon(productCouponCode, itemList, totalItemPrice, userId);
            getCantCombinedCouponTypeList(productCouponCode, CouponType.PRODUCT, cantCombinedCouponTypeList);
        }

        cantCombinedCouponTypeList.forEach(it -> {
            if (couponTypeUsingList.contains(it)) {
                // invalid
                throw new CustomException(ErrorConstant.COUPON_INVALID, "Cant combine coupons");
            }
        });
    }


    private CouponUsedEntity createCouponUsedEntity(String couponCode) {
        CouponEntity coupon = couponRepository.findByCodeAndIsDeletedFalse(couponCode)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Coupon with code " + couponCode));

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

        if (body.getTotal() < 10000 && body.getPaymentType() == PaymentType.VNPAY) {
            throw new CustomException(ErrorConstant.VNP_ERROR, ErrorConstant.VNPAY_MONEY_INVALID, "vnpay does not support bill payments under 10,000đ");
        }

        long totalPrice = 0L;
        String userId = SecurityUtils.getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, USER_ID_NOT_FOUND + userId));
        if (body.getCoin() == null) {
            body.setCoin(0L);
        }
        if (user.getCoin() < body.getCoin()) {
            throw new CustomException(ErrorConstant.INVALID_COIN_NUMBER, "User's coin count is less than the amount posted");
        }

        OrderBillEntity orderBill = modelMapperService.mapClass(body, OrderBillEntity.class);
        orderBill.setUser(user);
        orderBill.setOrderType(OrderType.SHIPPING);

        totalPrice = calculateOrderBill(body.getItemList(), orderBill);

        // set địa chỉ giao hàng
        AddressEntity address = addressRepository.findById(body.getAddressId())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ADDRESS_ID_NOT_FOUND + body.getAddressId()));
        ReceiverInformationEntity shippingInformation = new ReceiverInformationEntity();
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
        Time currentTime = DateUtils.getCurrentTime(ZoneId.of("GMT+7"));
        BranchEntity branch = branchService.getNearestBranchAndValidateTime(address.getLatitude(), address.getLongitude(), currentTime);
        orderBill.setBranch(branch);

        // set tổng hóa đơn và phí ship
        orderBill.setShippingFee(body.getShippingFee());
        orderBill.setTotalItemPrice(totalPrice);

        // tính phí vận chuyển
        totalPrice += body.getShippingFee();

        // xử lý coupon
        validateCouponForOrder(totalPrice, body.getItemList(), body.getOrderCouponCode(), body.getShippingCouponCode(), body.getProductCouponCode());
        long amountReduced = applyCouponForOrder(orderBill, body.getOrderCouponCode(), body.getShippingCouponCode(), body.getProductCouponCode());

        totalPrice -= amountReduced;

        if (totalPrice <= body.getCoin()) {
            orderBill.setCoin(totalPrice);
            totalPrice = 0;
        } else {
            totalPrice -= body.getCoin();
            orderBill.setCoin(body.getCoin());
        }

        // cập nhật lại xu cho user
        user.setCoin(user.getCoin() - orderBill.getCoin());
        userRepository.save(user);

        if (totalPrice != body.getTotal()) {
            throw new CustomException(ErrorConstant.ORDER_INVALID, "Total order is invalid");
        }
        orderBill.setTotalPayment(totalPrice);
        orderBill = orderBillRepository.save(orderBill);
        // set giao dịch
        buildTransaction(body.getPaymentType(), request, orderBill);
        TransactionEntity transaction = orderBill.getTransaction();
        transaction.setOrderBill(orderBill);

        OrderBillEntity dataSaved2 = orderBillRepository.save(orderBill);
        orderSearchService.upsertOrder(dataSaved2);
        transaction = dataSaved2.getTransaction();

        CreateOrderResponse resData = CreateOrderResponse.builder()
                .orderId(orderBill.getId())
                .branchId(orderBill.getBranch().getId())
                .transactionId(transaction.getId())
                .build();
        // set schedule for paymet
        if (transaction.getPaymentUrl() != null && totalPrice > 0) {
            Map<String, Object> checkTransactionData = new HashMap<String, Object>();
            resData.setPaymentUrl(transaction.getPaymentUrl());
            Instant checkTransactionTime = transaction.getCreatedAt().toInstant();
            if (transaction.getPaymentType() == PaymentType.ZALOPAY) {
                checkTransactionTime = DateUtils.after(checkTransactionTime, 15, ChronoUnit.MINUTES);
            } else if (transaction.getPaymentType() == PaymentType.VNPAY) {
                checkTransactionTime = DateUtils.after(checkTransactionTime, 16, ChronoUnit.MINUTES);
                checkTransactionTime = DateUtils.after(checkTransactionTime, 15, ChronoUnit.SECONDS);
            }
            checkTransactionData.put(TransactionQueryJob.TRANSACTION_ID, transaction.getId());
            checkTransactionData.put(TransactionQueryJob.PAYMENT_TYPE, transaction.getPaymentType());
            schedulerService.setScheduler(TransactionQueryJob.class, checkTransactionData, Date.from(checkTransactionTime));
        }

        Instant orderAcceptanceScheduleTime = DateUtils.after(dataSaved2.getCreatedAt().toInstant(), 30, ChronoUnit.MINUTES);

        // set schedule for accept order
        Map<String, Object> orderAcceptanceScheduleData = new HashMap<String, Object>();
        orderAcceptanceScheduleData.put(AcceptOrderJob.ORDER_BILL_ID, dataSaved2.getId());
        schedulerService.setScheduler(AcceptOrderJob.class, orderAcceptanceScheduleData, Date.from(orderAcceptanceScheduleTime));

        return resData;
    }

    @Transactional
    @Override
    public CreateOrderResponse createOnsiteOrder(CreateOnsiteOrderRequest body, HttpServletRequest request) {
        SecurityUtils.checkUserId(body.getUserId());

        if (body.getTotal() < 10000 && body.getPaymentType() == PaymentType.VNPAY) {
            throw new CustomException(ErrorConstant.VNP_ERROR, ErrorConstant.VNPAY_MONEY_INVALID, "vnpay does not support bill payments under 10,000đ");
        }

        long totalPrice = 0L;
        String userId = SecurityUtils.getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, USER_ID_NOT_FOUND + userId));

        if (body.getCoin() == null) {
            body.setCoin(0L);
        }
        if (user.getCoin() < body.getCoin()) {
            throw new CustomException(ErrorConstant.INVALID_COIN_NUMBER, "User's coin count is less than the amount posted");
        }

        OrderBillEntity orderBill = modelMapperService.mapClass(body, OrderBillEntity.class);
        orderBill.setUser(user);
        orderBill.setOrderType(OrderType.ONSITE);

        totalPrice = calculateOrderBill(body.getItemList(), orderBill);
        orderBill.setTotalItemPrice(totalPrice);

//        validateCouponForOrder(totalPrice, body.getItemList(), body.getOrderCouponCode(), null, body.getProductCouponCode());
        long amountReduced = applyCouponForOrder(orderBill, body.getOrderCouponCode(), null, body.getProductCouponCode());

        totalPrice -= amountReduced;

        if (totalPrice <= body.getCoin()) {
            orderBill.setCoin(totalPrice);
            totalPrice = 0;
        } else {
            totalPrice -= body.getCoin();
            orderBill.setCoin(body.getCoin());
        }

        // cập nhật lại xu cho user
        user.setCoin(user.getCoin() - orderBill.getCoin());
        userRepository.save(user);

        if (totalPrice != body.getTotal()) {
            throw new CustomException(ErrorConstant.ORDER_INVALID, "Total order is invalid");
        }

        // set total payment
        orderBill.setTotalPayment(totalPrice);
        orderBill = orderBillRepository.save(orderBill);

        // set giao dịch
        buildTransaction(body.getPaymentType(), request, orderBill);
        TransactionEntity transaction = orderBill.getTransaction();
        transaction.setOrderBill(orderBill);


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
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.BRANCH_ID_NOT_FOUND + body.getBranchId()));
        orderBill.setBranch(branch);


        // set thong tin nhan hang
        orderBill.setShippingInformation(ReceiverInformationEntity.builder()
                .phoneNumber(user.getPhoneNumber())
                .receiveTime(body.getReceiveTime())
                .recipientName(user.getFullName())
                .orderBill(orderBill)
                .build());


        OrderBillEntity dataSaved = orderBillRepository.save(orderBill);
        orderSearchService.upsertOrder(dataSaved);
        transaction = dataSaved.getTransaction();

        CreateOrderResponse resData = CreateOrderResponse.builder()
                .orderId(orderBill.getId())
                .transactionId(transaction.getId())
                .build();
        if (transaction.getPaymentUrl() != null && totalPrice > 0) {
            resData.setPaymentUrl(transaction.getPaymentUrl());
        }

        Instant newIn = DateUtils.after(dataSaved.getCreatedAt().toInstant(), 30, ChronoUnit.MINUTES);

        Map<String, Object> orderAcceptanceData = new HashMap<String, Object>();
        orderAcceptanceData.put(AcceptOrderJob.ORDER_BILL_ID, dataSaved.getId());
        schedulerService.setScheduler(AcceptOrderJob.class, orderAcceptanceData, Date.from(newIn));

        return resData;
    }

    @Override
    public GetOrderHistoryForEmployeeResponse getOrderHistoryPageForEmployee(OrderStatus orderStatus, int page, int size, String key) {
        String statusRegex = RegexUtils.generateFilterRegexString(orderStatus != null ? orderStatus.toString() : "");
        Pageable pageable = PageRequest.of(page - 1, size);
        GetOrderHistoryForEmployeeResponse data = new GetOrderHistoryForEmployeeResponse();

        if (key != null) {
            Page<OrderIndex> orderPage = orderSearchService.searchOrderForAdmin(key, page, size, statusRegex);
            data.setTotalPage(orderPage.getTotalPages());
            data.setOrderList(modelMapperService.mapList(orderPage.getContent(), GetOrderHistoryForEmployeeResponse.OrderInfo.class));
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
        List<OrderStatus> validOrderStatus = Arrays.asList(OrderStatus.ACCEPTED, OrderStatus.DELIVERING, OrderStatus.SUCCEED, OrderStatus.CANCELED);

        if (!validOrderStatus.contains(body.getOrderStatus())) {
            throw new CustomException(ErrorConstant.DATA_SEND_INVALID, "Order status is not valid");
        }


        OrderBillEntity order = orderBillRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + orderId));

        if (order.getRequestCancellation() != null && order.getRequestCancellation().getStatus() == CancellationRequestStatus.PENDING) {
            throw new CustomException(ErrorConstant.ACTING_INCORRECTLY, "You must to process cancellation request from user");
        }

        order.getOrderEventList().add(OrderEventEntity.builder()
                .orderStatus(body.getOrderStatus())
                .description(body.getDescription())
                .orderBill(order)
                .isEmployee(true)
                .build()
        );

        TransactionEntity transaction = order.getTransaction();
        if (body.getOrderStatus() == OrderStatus.CANCELED && transaction.getStatus() == PaymentStatus.PAID) {
            UserEntity user = order.getUser();
            user.setCoin(user.getCoin() + order.getTotalPayment());
            userRepository.save(user);
            transaction.setStatus(PaymentStatus.REFUNDED);
            transactionRepository.save(transaction);
        }
        OrderBillEntity updatedOrder = orderBillRepository.save(order);
        orderSearchService.upsertOrder(updatedOrder);

    }

    @Override
    public void createCancellationRequest(CreateCancellationDemandRequest body, String orderId) {
        OrderBillEntity orderBill = orderBillRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + orderId));

        if (orderBill.getOrderEventList() != null && orderBill.getOrderEventList().size() == 2 && orderBill.getOrderEventList().get(1).getOrderStatus() == OrderStatus.ACCEPTED) {
            throw new CustomException(ErrorConstant.ACTING_INCORRECTLY, "Cannot create cancellation request when order status is not \"ACCEPTED\"");
        }


        UserEntity user = orderBill.getUser();
        SecurityUtils.checkUserId(user.getId());

        CancellationRequestEntity cancellationRequestEntity = CancellationRequestEntity.builder()
                .reason(body.getReason())
                .status(CancellationRequestStatus.PENDING)
                .orderBill(orderBill)
                .build();

        cancellationDemandRepository.save(cancellationRequestEntity);

        orderBill.getOrderEventList().add(OrderEventEntity.builder()
                .description("Customer creates a request to cancel the order")
                .isEmployee(false)
                .orderBill(orderBill)
                .orderStatus(OrderStatus.CANCELLATION_REQUEST)
                .build());

        orderBill = orderBillRepository.save(orderBill);
        orderSearchService.upsertOrder(orderBill);
    }

    @Override
    public void processCancellationRequest(ProcessCancellationDemandRequest body, String orderId) {
        if (body.getStatus() == CancellationRequestStatus.PENDING) {
            throw new CustomException(ErrorConstant.DATA_SEND_INVALID);
        }

        OrderBillEntity orderBill = orderBillRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + orderId));

        CancellationRequestEntity cancellationRequestEntity = orderBill.getRequestCancellation();

        if (cancellationRequestEntity != null && cancellationRequestEntity.getStatus() != CancellationRequestStatus.PENDING) {
            throw new CustomException(ErrorConstant.ACTING_INCORRECTLY, "The request has already been processed");
        }

        UserEntity customer = orderBill.getUser();
        if (cancellationRequestEntity != null) {
            cancellationRequestEntity.setStatus(body.getStatus());
            if (body.getStatus() == CancellationRequestStatus.ACCEPTED) {
                orderBill.getOrderEventList().add(OrderEventEntity.builder()
                        .description("Employee agreed to cancel the order")
                        .isEmployee(true)
                        .orderBill(orderBill)
                        .orderStatus(OrderStatus.CANCELLATION_REQUEST_ACCEPTED)
                        .build());

                orderBill.getOrderEventList().add(OrderEventEntity.builder()
                        .description("Customer requests to cancel order")
                        .isEmployee(true)
                        .orderBill(orderBill)
                        .orderStatus(OrderStatus.CANCELED)
                        .build());
                if (orderBill.getTransaction().getStatus() == PaymentStatus.PAID) {
                    customer.setCoin(orderBill.getTotalPayment() + customer.getCoin());
                    userRepository.save(customer);

                    orderBill.getTransaction().setStatus(PaymentStatus.REFUNDED);
                }

            } else {
                orderBill.getOrderEventList().add(OrderEventEntity.builder()
                        .description("Employee refused the request to cancel the order")
                        .isEmployee(true)
                        .orderBill(orderBill)
                        .orderStatus(OrderStatus.CANCELLATION_REQUEST_REFUSED)
                        .build());
            }
            OrderBillEntity updatedOrder = orderBillRepository.save(orderBill);
            orderSearchService.upsertOrder(updatedOrder);
        } else {
            throw new CustomException(ErrorConstant.NOT_FOUND, "Cancellation request is not exist");
        }
    }

    @Override
    public void cancelOrder(String orderId, CancelOrderBillRequest body) {
        OrderBillEntity orderBill = orderBillRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + orderId));

        if (DateUtils.isPassed30Minutes(orderBill.getCreatedAt())) {
            throw new CustomException(ErrorConstant.ACTING_INCORRECTLY, "Cant cancel order after 30 minutes");
        }

        UserEntity user = orderBill.getUser();
        SecurityUtils.checkUserId(user.getId());

        orderBill.getOrderEventList().add(OrderEventEntity.builder()
                .orderStatus(OrderStatus.CANCELED)
                .description(body.getDescription())
                .orderBill(orderBill)
                .isEmployee(false)
                .build()
        );

        if (orderBill.getTransaction().getPaymentType() != PaymentType.CASHING && orderBill.getTransaction().getStatus() == PaymentStatus.PAID) {
            user.setCoin(user.getCoin() + orderBill.getTotalPayment());
            userRepository.save(user);
            orderBill.getTransaction().setStatus(PaymentStatus.REFUNDED);
        }


        OrderBillEntity updatedOrder = orderBillRepository.save(orderBill);
        orderSearchService.upsertOrder(updatedOrder);
    }

    @Override
    public GetOrderQueueResponse getShippingOrderQueueToday(OrderStatus orderStatus, int page, int size) {
        String employeeId = SecurityUtils.getCurrentUserId();
        GetOrderQueueResponse data = new GetOrderQueueResponse();
        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + employeeId));
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
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + employeeId));
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
            Page<OrderIndex> orderPage = orderSearchService.searchOrderForAdmin(key, page, size, statusRegex);
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
    public GetOrderByIdResponse getOrderDetailsById(String id) {
        OrderBillEntity orderBill = orderBillRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + id));
        GetOrderByIdResponse order = GetOrderByIdResponse.fromOrderBillEntity(orderBill);
        return order;
    }

    @Override
    public GetShippingFeeResponse getShippingFee(Double lat, Double lng) {
        GetShippingFeeResponse data = new GetShippingFeeResponse();
        List<BranchEntity> branchEntityList = branchRepository.findByStatus(BranchStatus.ACTIVE);
        String clientCoordinates = lat + "," + lng;
        List<String> destinationCoordinatesList = LocationUtils.getCoordinatesListFromBranchList(branchEntityList);
        List<DistanceMatrixResponse.Row.Element.Distance> distanceList = goongService.getDistanceFromClientToBranches(clientCoordinates, destinationCoordinatesList, "bike");
        int branchSize = branchEntityList.size();


        Time currentTime = DateUtils.getCurrentTime(ZoneId.of("GMT+7"));
        BranchEntity branchEntity = branchService.getNearestBranchAndValidateTime(lat, lng, currentTime);
        // TODO: tính tien ship tu ben thu 3

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
            List<String> statusesToAdd = Arrays.asList(OrderStatus.ACCEPTED.name(), OrderStatus.DELIVERING.name(),
                    OrderStatus.CANCELLATION_REQUEST.name(), OrderStatus.CANCELLATION_REQUEST_ACCEPTED.name(),
                    OrderStatus.CANCELLATION_REQUEST_REFUSED.name());
            orderStatusList.addAll(statusesToAdd);
        } else if (orderPhasesStatus == OrderPhasesStatus.SUCCEED) {
            orderStatusList.add(OrderStatus.SUCCEED.name());
        } else if (orderPhasesStatus == OrderPhasesStatus.CANCELED) {
            orderStatusList.add(OrderStatus.CANCELED.name());
        }
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
