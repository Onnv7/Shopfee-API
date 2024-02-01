package com.hcmute.shopfee.service.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.dto.request.CreateOnsiteOrderRequest;
import com.hcmute.shopfee.dto.request.CreateShippingOrderRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.entity.*;
import com.hcmute.shopfee.entity.order.*;
import com.hcmute.shopfee.entity.product.ProductEntity;
import com.hcmute.shopfee.entity.product.SizeEntity;
import com.hcmute.shopfee.entity.product.ToppingEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.model.elasticsearch.OrderIndex;
import com.hcmute.shopfee.repository.database.*;
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
        response.setOrderQuantity((int)current);

        Date startDatePrev = DateUtils.createDateTimeByToday(0, 0, 0, 0, -1);
        Date endDatePrev = DateUtils.createDateTimeByToday(23, 59, 59, 999, -1);

        long prev = orderBillRepository.countOrderInCurrentDateByStatus(orderStatus.name(), DateUtils.formatYYYYMMDD(startDatePrev));
        long difference = current - prev;
        response.setDifference((int) difference);
        return response;
    }
}
