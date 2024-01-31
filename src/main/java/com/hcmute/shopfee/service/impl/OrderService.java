package com.hcmute.shopfee.service.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.dto.request.CreateOnsiteOrderRequest;
import com.hcmute.shopfee.dto.request.CreateShippingOrderRequest;
import com.hcmute.shopfee.dto.response.CreateOrderResponse;
import com.hcmute.shopfee.entity.AddressEntity;
import com.hcmute.shopfee.entity.BranchEntity;
import com.hcmute.shopfee.entity.TransactionEntity;
import com.hcmute.shopfee.entity.UserEntity;
import com.hcmute.shopfee.entity.order.*;
import com.hcmute.shopfee.entity.product.ProductEntity;
import com.hcmute.shopfee.entity.product.SizeEntity;
import com.hcmute.shopfee.entity.product.ToppingEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.AddressRepository;
import com.hcmute.shopfee.repository.database.BranchRepository;
import com.hcmute.shopfee.repository.database.TransactionRepository;
import com.hcmute.shopfee.repository.database.UserRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.service.IOrderService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.CalculateUtils;
import com.hcmute.shopfee.utils.SecurityUtils;
import com.hcmute.shopfee.utils.VNPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
                    .filter(it -> it.getSize().equals(orderItemDto.getSize()))
                    .findFirst().orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + orderItemDto.getSize()));
            item.setPrice(sizeItem.getPrice());

            totalPrice += (long) ((sizeItem.getPrice() + totalPriceToppings) * orderItemDto.getQuantity());

            item.setOrderBill(orderBill);
        }

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
                .build());
        orderBill.setOrderEventList(orderEventList);


        BranchEntity branch = branchRepository.findById(body.getBranchId())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getBranchId()));
        orderBill.setBranch(branch);

        orderBill.setReceiveTime(Date.from(body.getReceiveTime().atZone(ZoneId.systemDefault()).toInstant()));


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
}
