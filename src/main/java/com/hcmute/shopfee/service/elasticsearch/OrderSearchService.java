package com.hcmute.shopfee.service.elasticsearch;

import com.hcmute.shopfee.entity.database.UserEntity;
import com.hcmute.shopfee.entity.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.database.order.OrderEventEntity;
import com.hcmute.shopfee.entity.database.order.OrderItemEntity;
import com.hcmute.shopfee.entity.database.product.ProductEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.entity.elasticsearch.OrderIndex;
import com.hcmute.shopfee.repository.elasticsearch.OrderSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderSearchService {
    private final OrderSearchRepository orderSearchRepository;

    public OrderIndex createOrder(OrderBillEntity orderBillEntity) {
        UserEntity user = orderBillEntity.getUser();

        List<OrderItemEntity> orderItemList = orderBillEntity.getOrderItemList();
        ProductEntity product = orderItemList.get(0).getProduct();

       OrderIndex orderIndex = OrderIndex.builder()
                .id(orderBillEntity.getId())
//                .code(orderBillEntity.getCode())
                .customerName(user.getFullName())
                .recipientName(orderBillEntity.getShippingInformation().getRecipientName())
                .phoneNumber(user.getPhoneNumber())
                .phoneNumberReceiver(orderBillEntity.getShippingInformation().getPhoneNumber())
                .orderType(orderBillEntity.getOrderType())
                .statusLastEvent(OrderStatus.CREATED)
                .total(orderBillEntity.getTotalItemPrice())
                .productQuantity(orderItemList.size())
                .productThumbnail(product.getImage().getThumbnailUrl())
                .customerCode(user.getId())
                .timeLastEvent(orderBillEntity.getOrderEventList().get(0).getCreatedAt())
                .createdAt(orderBillEntity.getCreatedAt())
                .email(user.getEmail())
                .build();
        return orderSearchRepository.save(orderIndex);
    }

    public void upsertOrder(OrderBillEntity orderBillEntity) {
        UserEntity user = orderBillEntity.getUser();
        List<OrderItemEntity> orderItemList = orderBillEntity.getOrderItemList();
        ProductEntity product = orderItemList.get(0).getProduct();
        OrderIndex order = orderSearchRepository.findById(orderBillEntity.getId()).orElse(null);

        OrderEventEntity lastStatus = orderBillEntity.getOrderEventList().get(0);
        if (order != null) {
            order.setCustomerName(user.getFullName());
            order.setRecipientName(orderBillEntity.getShippingInformation().getRecipientName());
            order.setPhoneNumber(orderBillEntity.getShippingInformation().getPhoneNumber());
            order.setOrderType(orderBillEntity.getOrderType());
            order.setStatusLastEvent(lastStatus.getOrderStatus());
            order.setTotal(orderBillEntity.getTotalItemPrice());
            order.setProductQuantity(orderItemList.size());
            order.setProductThumbnail(product.getImage().getThumbnailUrl());
            order.setCustomerCode(user.getId());
            order.setTimeLastEvent(lastStatus.getCreatedAt());
            orderSearchRepository.save(order);
        } else {
            createOrder(orderBillEntity);
        }
    }


    public Page<OrderIndex> searchOrderForAdmin(String key, int page, int size, String status) {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        return orderSearchRepository.searchOrderForAdmin(key, status, pageable);
    }
}
