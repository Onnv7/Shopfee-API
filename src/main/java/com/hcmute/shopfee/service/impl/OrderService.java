package com.hcmute.shopfee.service.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.dto.request.CreateOnsiteOrderRequest;
import com.hcmute.shopfee.dto.request.CreateShippingOrderRequest;
import com.hcmute.shopfee.dto.request.ValidateCouponForOrderRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.entity.*;
import com.hcmute.shopfee.entity.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.coupon.CouponEntity;
import com.hcmute.shopfee.entity.coupon.condition.*;
import com.hcmute.shopfee.entity.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.order.*;
import com.hcmute.shopfee.entity.product.ProductEntity;
import com.hcmute.shopfee.entity.product.SizeEntity;
import com.hcmute.shopfee.entity.product.ToppingEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.model.elasticsearch.OrderIndex;
import com.hcmute.shopfee.repository.database.*;
import com.hcmute.shopfee.repository.database.coupon.CouponConditionRepository;
import com.hcmute.shopfee.repository.database.coupon.CouponRepository;
import com.hcmute.shopfee.repository.database.coupon_used.CouponUsedRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.service.IOrderService;
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

import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
            OrderItemEntity item = modelMapperService.mapClass(orderItemDto, OrderItemEntity.class);
            long totalPriceToppings = 0;
            ProductEntity productInfo = productRepository.findByIdAndIsDeletedFalse(orderItemDto.getProductId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + orderItemDto.getProductId()));
            item.setName(productInfo.getName());

            List<ToppingEntity> toppingList = productInfo.getToppingList();
            List<SizeEntity> sizeList = productInfo.getSizeList();

            List<ItemToppingEntity> itemsToppingList = new ArrayList<>();

            for (String toppingName : orderItemDto.getToppingNameList()) {
                ItemToppingEntity itemTopping = new ItemToppingEntity();
                ToppingEntity toppingEntity = toppingList.stream()
                        .filter(topping -> toppingName.equals(topping.getName()))
                        .findFirst()
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + toppingName));
                itemTopping.setName(toppingName);
                itemTopping.setPrice(toppingEntity.getPrice());
                itemTopping.setOrderItem(item);
                itemsToppingList.add(itemTopping);
                totalPriceToppings += toppingEntity.getPrice();
            }
            SizeEntity sizeItem = sizeList.stream()
                    .filter(it -> it.getSize() == orderItemDto.getSize())
                    .findFirst().orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + orderItemDto.getSize()));
            item.setPrice(sizeItem.getPrice());

            totalPrice += (long) ((sizeItem.getPrice() + totalPriceToppings) * orderItemDto.getQuantity());
            item.setItemToppingList(itemsToppingList);
            item.setOrderBill(orderBill);
            item.setProduct(productInfo);
            orderItemEntityList.add(item);
        }

        orderBill.setOrderItemList(orderItemEntityList);

        return totalPrice;
    }

    private void checkMain(List<CouponEntity> sameCouponList, CouponType couponType) {
        CombinationType combinationType;
        if (couponType == CouponType.ORDER) {
            combinationType = CombinationType.ORDER;
        } else if (couponType == CouponType.PRODUCT) {
            combinationType = CombinationType.PRODUCT;
        } else {
            combinationType = CombinationType.SHIPPING;
        }

        sameCouponList.forEach(coupon -> {
            coupon.getConditionList().forEach(cond -> {
                List<CombinationConditionEntity> combinationConditionList = cond.getCombinationConditionList();
                if (combinationConditionList.isEmpty()) {
                    throw new CustomException(ErrorConstant.COUPON_INVALID);
                }
                combinationConditionList.stream().filter(com -> com.getType() == combinationType)
                        .findFirst().orElseThrow(() -> new CustomException(ErrorConstant.COUPON_INVALID));
            });
        });
    }


    private boolean checkMinPurchaseCondition(MinPurchaseConditionEntity minPurchaseCondition, long totalOrderBill, int itemCount) {
        if (minPurchaseCondition.getType() == MiniPurchaseType.NONE) {
            return true;
        } else if (minPurchaseCondition.getType() == MiniPurchaseType.MONEY) {
            if (totalOrderBill < minPurchaseCondition.getValue()) {
                return false;
            }
        } else {
            if (itemCount < minPurchaseCondition.getValue()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkUsageCondition(String userId, String couponCode, List<UsageConditionEntity> usageConditionList) {
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

    private boolean checkEligibilityCustomerCondition(EligibilityCustomerConditionEntity condition, String userId) {
        if (condition.getType() == ApplicableCustomerType.ALL) {
            return true;
        } else if (condition.getType() == ApplicableCustomerType.ONE) {
            // TODO: check ONE
        } else {
            // TODO: check GROUP
        }
        return true;
    }

    private boolean checkCombinationCondition(List<CouponEntity> couponList) {
        CouponType[] types = {CouponType.ORDER, CouponType.PRODUCT, CouponType.SHIPPING};

        int[][] array = new int[3][3];

        for (CouponEntity coupon : couponList) {
            CouponType couponType = coupon.getCouponType();
            int col = couponType == CouponType.ORDER ? 0 :
                    couponType == CouponType.PRODUCT ? 1 : 2;
            for (CouponConditionEntity condition : coupon.getConditionList()) {
                if (condition.getType() != ConditionType.COMBINATION) {
                    continue;
                }
                List<CombinationConditionEntity> combinationList = condition.getCombinationConditionList();
                if (combinationList != null) {
                    combinationList.forEach(combination -> {
                        CombinationType combinationType = combination.getType();
                        int row = combinationType == CombinationType.ORDER ? 0 :
                                combinationType == CombinationType.PRODUCT ? 1 : 2;
                        array[row][col]++;
                    });
                }
            }
        }

        for (CouponType type : types) {
            List<CouponEntity> sameCouponeList = couponList.stream()
                    .filter(coupon -> coupon.getCouponType() == type)
                    .toList();
            if (sameCouponeList.size() <= 1) {
                continue;
            }
            if ((type == CouponType.ORDER && array[0][0] != sameCouponeList.size()) ||
                    (type == CouponType.PRODUCT && array[1][1] != sameCouponeList.size()) ||
                    (type == CouponType.SHIPPING && array[2][2] != sameCouponeList.size())
            ) {
                return false;
            }

        }


        List<CouponEntity> orderCouponeList = couponList.stream()
                .filter(coupon -> coupon.getCouponType() == CouponType.ORDER)
                .toList();
        List<CouponEntity> productCouponList = couponList.stream()
                .filter(coupon -> coupon.getCouponType() == CouponType.PRODUCT)
                .toList();
        List<CouponEntity> shippingCouponList = couponList.stream()
                .filter(coupon -> coupon.getCouponType() == CouponType.SHIPPING)
                .toList();
        if (orderCouponeList.size() * productCouponList.size() > 0) {
            if (orderCouponeList.size() != array[1][0] || productCouponList.size() != array[0][1]) {
                return false;
            }
        }
        if (orderCouponeList.size() * shippingCouponList.size() > 0) {
            if (orderCouponeList.size() != array[2][0] || shippingCouponList.size() != array[0][2]) {
                return false;
            }
        }
        if (productCouponList.size() * shippingCouponList.size() > 0) {
            if (productCouponList.size() != array[2][1] || shippingCouponList.size() != array[1][2]) {
                return false;
            }
        }

        return true;
    }

    private boolean checkTargetObjectCondition(List<TargetObjectConditionEntity> conditions, String productId) {
        for (TargetObjectConditionEntity condition : conditions) {
            if (condition.getType() == TargetType.PRODUCT) {
                if (!condition.getValue().equals(productId)) {
                    return false;
                }
            } else if (condition.getType() == TargetType.CATEGORY) {
                ProductEntity product = productRepository.findByIdAndCategory_IdAndIsDeletedFalse(productId, condition.getValue()).orElse(null);
                if (product == null) {
                    return false;
                }
            }
        }
        return true;
    }


    public void validateCoupon(CouponEntity coupon, long total, int itemSize) {
        coupon.getConditionList().forEach(it -> {
            if (it.getUsageConditionList() != null) {
                if (!checkUsageCondition(SecurityUtils.getCurrentUserId(), coupon.getCode(), it.getUsageConditionList())) {
                    throw new CustomException(ErrorConstant.COUPON_INVALID);
                }
            } else if (it.getMinPurchaseCondition() != null) {
                if (!checkMinPurchaseCondition(it.getMinPurchaseCondition(), total, itemSize)) {
                    throw new CustomException(ErrorConstant.COUPON_INVALID);
                }
            } else if (it.getApplicableCustomerCondition() != null) {
                if (!checkEligibilityCustomerCondition(it.getApplicableCustomerCondition(), SecurityUtils.getCurrentUserId())) {
                    throw new CustomException(ErrorConstant.COUPON_INVALID);
                }
            } else if (it.getTargetObjectConditionList() != null) {
                if (!checkTargetObjectCondition(it.getTargetObjectConditionList(), SecurityUtils.getCurrentUserId())) {
                    throw new CustomException(ErrorConstant.COUPON_INVALID);
                }
            }
        });
    }

    public void validateCouponForOrder(ValidateCouponForOrderRequest body) {
        CouponEntity orderCoupon = body.getOrderCouponCode() != null ? couponRepository.findByCodeAndIsDeletedFalse(body.getOrderCouponCode())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getOrderCouponCode())) : null;
        CouponEntity shippingCoupon = body.getShippingCouponCode() != null ? couponRepository.findByCodeAndIsDeletedFalse(body.getShippingCouponCode())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getShippingCouponCode())) : null;
        CouponEntity productCoupon = body.getProductCouponCode() != null ? couponRepository.findByCodeAndIsDeletedFalse(body.getProductCouponCode())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getProductCouponCode())) : null;
        if (orderCoupon != null)
            validateCoupon(orderCoupon, body.getTotal(), body.getItemList().size());
        if (shippingCoupon != null)
            validateCoupon(shippingCoupon, body.getTotal(), body.getItemList().size());
        if (productCoupon != null)
            validateCoupon(productCoupon, body.getTotal(), body.getItemList().size());
    }

    public void checkOrderCoupon(String couponCode, String userId, long totalOrderBill, int itemListSize) {
        int discountMoney = 0;
        CouponEntity couponEntity = couponRepository.findByCodeAndIsDeletedFalse(couponCode)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + couponCode));
        List<CouponConditionEntity> conditionList = couponEntity.getConditionList();

        for (CouponConditionEntity condition : conditionList) {
            ConditionType conditionType = condition.getType();
            switch (conditionType) {
                case USAGE -> {
                    List<UsageConditionEntity> usageConditionList = condition.getUsageConditionList();
                    // checkUsageConditionCoupon(userId, couponCode, usageConditionList);
                    for (UsageConditionEntity usageCondition : usageConditionList) {
                        UsageConditionType type = usageCondition.getType();
                        switch (type) {
                            case QUANTITY -> {
                                if (usageCondition.getValue() <= 0) {
                                    throw new CustomException(ErrorConstant.COUPON_INVALID);
                                } else {
                                    usageCondition.setValue(usageCondition.getValue() - 1);
                                }
                            }
                            case LIMIT_ONE_FOR_USER -> {
                                List<CouponUsedEntity> couponUsedEntityList = couponUsedRepository.getCouponUsedByUserIdAndCode(userId, couponCode);
                                if (!couponUsedEntityList.isEmpty()) {
                                    throw new CustomException(ErrorConstant.COUPON_INVALID);
                                }
                            }
                        }
                    }
                }
                case MIN_PURCHASE -> {
                    MiniPurchaseType type = condition.getMinPurchaseCondition().getType();
                    if (type == MiniPurchaseType.MONEY) {
                        if (condition.getMinPurchaseCondition().getValue() < totalOrderBill) {
                            throw new CustomException(ErrorConstant.COUPON_INVALID);
                        }
                    } else if (type == MiniPurchaseType.QUANTITY) {
                        if (condition.getMinPurchaseCondition().getValue() < itemListSize) {
                            throw new CustomException(ErrorConstant.COUPON_INVALID);
                        }
                    }
                }
                case APPLICABLE_CUSTOMER -> {
                    ApplicableCustomerType type = condition.getApplicableCustomerCondition().getType();
                    // TODO: triển khai check điều kiện này
                }
                default -> {
                    throw new CustomException(ErrorConstant.COUPON_INVALID);
                }

            }
            couponConditionRepository.save(condition);
        }

    }

    @Transactional
    @Override
    public CreateOrderResponse createShippingOrder(CreateShippingOrderRequest body, HttpServletRequest request) {
        SecurityUtils.checkUserId(body.getUserId());
        long totalPrice = 0L;
        String userId = SecurityUtils.getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + userId));
        OrderBillEntity orderBill = modelMapperService.mapClass(body, OrderBillEntity.class);
        orderBill.setUser(user);
        orderBill.setOrderType(OrderType.SHIPPING);

        totalPrice = calculateOrderBill(body.getItemList(), orderBill);
        orderBill.setTotal(totalPrice);


        AddressEntity address = addressRepository.findById(body.getAddressId())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getAddressId()));
        ShippingInformationEntity shippingInformation = new ShippingInformationEntity();
//        modelMapperService.map(address, shippingInformation);
        shippingInformation.fromAddressEntity(address);

        shippingInformation.setOrderBill(orderBill);
        orderBill.setShippingInformation(shippingInformation);

        Map<String, Object> transactionBuilderMap = buildTransaction(body.getPaymentType(), request, totalPrice);
//        transaction = transactionRepository.save(transaction);
        TransactionEntity transaction = (TransactionEntity) transactionBuilderMap.get("transaction");
        transaction.setOrderBill(orderBill);
        orderBill.setTransaction(transaction);

        List<OrderEventEntity> orderEventList = new ArrayList<>();
        orderEventList.add(OrderEventEntity.builder()
                .orderStatus(OrderStatus.CREATED)
                .description("Order created successfully")
                .orderBill(orderBill)
                .isEmployee(false)
                .build());
        orderBill.setOrderEventList(orderEventList);


        BranchEntity branch = getNearestBranches(address);
        orderBill.setBranch(branch);

        totalPrice += body.getShippingFee();

        if (totalPrice != body.getTotal()) {
            throw new CustomException(ErrorConstant.TOTAL_ORDER_INVALID);
        }

        orderBillRepository.save(orderBill);

        CreateOrderResponse resData = CreateOrderResponse.builder()
                .orderId(orderBill.getId())
                .transactionId(transaction.getId())
                .build();
        if (transactionBuilderMap.get(VNP_URL_KEY) != null) {
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
        orderBill.setTotal(totalPrice);


        Map<String, Object> transactionBuilderMap = buildTransaction(body.getPaymentType(), request, totalPrice);
//        transaction = transactionRepository.save(transaction);
        TransactionEntity transaction = (TransactionEntity) transactionBuilderMap.get("transaction");
        transaction.setOrderBill(orderBill);
        orderBill.setTransaction(transaction);

        List<OrderEventEntity> orderEventList = new ArrayList<>();
        orderEventList.add(OrderEventEntity.builder()
                .orderStatus(OrderStatus.CREATED)
                .description("Order created successfully")
                .isEmployee(false)
                .orderBill(orderBill)
                .build());
        orderBill.setOrderEventList(orderEventList);


        BranchEntity branch = branchRepository.findById(body.getBranchId())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getBranchId()));
        orderBill.setBranch(branch);

        orderBill.setReceiveTime(body.getReceiveTime());


        if (totalPrice != body.getTotal()) {
            throw new CustomException(ErrorConstant.TOTAL_ORDER_INVALID);
        }

        orderBillRepository.save(orderBill);

        CreateOrderResponse resData = CreateOrderResponse.builder()
                .orderId(orderBill.getId())
                .transactionId(transaction.getId())
                .build();
        if (transactionBuilderMap.get(VNP_URL_KEY) != null) {
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

    @Override
    public void addNewOrderEvent(String id, OrderStatus orderStatus, String description) {
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
        List<OrderEventEntity> orderEventEntityList = orderEventRepository.findByOrderBill_Id(orderId);
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
