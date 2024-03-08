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
import com.hcmute.shopfee.repository.database.*;
import com.hcmute.shopfee.repository.database.coupon.CouponRepository;
import com.hcmute.shopfee.repository.database.coupon.condition.CombinationConditionRepository;
import com.hcmute.shopfee.repository.database.coupon_used.CouponUsedRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.service.common.GoongService;
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

import static com.hcmute.shopfee.constant.ErrorConstant.USER_ID_NOT_FOUND;
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
    private final GoongService goongService;
    private final CombinationConditionRepository combinationConditionRepository;
    private final BranchService branchService;

    public BranchEntity getNearestBranches(AddressEntity address) {
        // TODO: xem có status thì check status cửa hàng
        List<BranchEntity> allBranches = branchRepository.findByStatus(BranchStatus.ACTIVE);
        List<String> destinationCoordinatesList = branchService.getCoordinatesListFromBranchList(allBranches);
        String clientCoordinates = address.getLatitude() + "," + address.getLongitude();
        List<DistanceMatrixResponse.Row.Element.Distance> distanceList = goongService.getDistanceFromClientToBranches(clientCoordinates, destinationCoordinatesList, "bike");
        int branchListSize = allBranches.size();
        int minDistance = distanceList.get(0).getValue();
        int minIndexBranch = 0;
        for(int i = 0 ; i <branchListSize; i++ ) {
            if(distanceList.get(i).getValue() > 12000) {
                continue;
            }

            if(distanceList.get(i).getValue() < minDistance) {
                minDistance = distanceList.get(i).getValue();
                minIndexBranch = i;
            }
        }

        if(minDistance > 12000) {
            throw new CustomException(ErrorConstant.NOT_FOUND, "Your location is outside the service area");
        }

        return allBranches.get(minIndexBranch);

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
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.PRODUCT_ID_NOT_FOUND + orderItemDto.getProductId()));

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
                        throw new CustomException(ErrorConstant.COUPON_INVALID, "Not found subject " + subjectConditionEntity.getObjectId() );
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
        validateCouponForOrder(totalPrice, body.getItemList(), body.getOrderCouponCode(), body.getShippingCouponCode(), body.getProductCouponCode());
        long amountReduced = applyCouponForOrder(orderBill, body.getOrderCouponCode(), body.getShippingCouponCode(), body.getProductCouponCode());

        totalPrice -= amountReduced;

        if (totalPrice < body.getCoin()) {
            orderBill.setCoin(totalPrice);
            totalPrice = 0;
        } else {
            totalPrice -= body.getCoin();
            orderBill.setCoin(body.getCoin());
        }
        user.setCoin(orderBill.getCoin());
        userRepository.save(user);

        if (totalPrice != body.getTotal()) {
            throw new CustomException(ErrorConstant.ORDER_INVALID, "Total order is invalid");
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

        if (totalPrice < body.getCoin()) {
            orderBill.setCoin(totalPrice);
            totalPrice = 0;
        } else {
            totalPrice -= body.getCoin();
            orderBill.setCoin(body.getCoin());
        }
        user.setCoin(orderBill.getCoin());
        userRepository.save(user);

        if (totalPrice != body.getTotal()) {
            throw new CustomException(ErrorConstant.ORDER_INVALID, "Total order is invalid");
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
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.BRANCH_ID_NOT_FOUND + body.getBranchId()));
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
    public void addNewOrderEvent(String id, OrderStatus orderStatus, String description, HttpServletRequest request) {
        OrderBillEntity order = orderBillRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + id));

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
        if (orderStatus == OrderStatus.CANCELED && transaction.getPaymentType() == PaymentType.BANKING_VNPAY) {
//            VNPayUtils.refund(request, transaction.getTimeCode(), order.getTotalPayment().toString(), transaction.getInvoiceCode(), "02");
            try {
                VNPayUtils.refund(request, transaction.getTimeCode(), order.getTotalPayment().toString(), transaction.getInvoiceCode(), "02");
            } catch (IOException e) {
                throw new CustomException(ErrorConstant.VNP_ERROR, "Refund failed");
            }
        }
    }

    @Override
    public List<GetShippingOrderQueueResponse> getShippingOrderQueueToday(OrderStatus orderStatus, int page, int size) {
        String employeeId = SecurityUtils.getCurrentUserId();

        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + employeeId));
        Long branchId = employeeEntity.getBranch().getId();

        Pageable pageable = PageRequest.of(page - 1, size);
        List<OrderBillEntity> orderList = orderBillRepository.getShippingOrderQueueToday(orderStatus.name(), branchId, OrderType.SHIPPING.name(), pageable).getContent();

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

        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + employeeId));
        Long branchId = employeeEntity.getBranch().getId();


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
        OrderBillEntity orderBill = orderBillRepository.findById(id).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + id));
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
