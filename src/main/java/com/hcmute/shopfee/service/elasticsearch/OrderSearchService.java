//package com.hcmute.shopfee.service.elasticsearch;
//
//import com.hcmute.shopfee.collection.OrderCollection;
//import com.hcmute.shopfee.collection.OrderItemCollection;
//import com.hcmute.shopfee.collection.ProductCollection;
//import com.hcmute.shopfee.collection.UserCollection;
//import com.hcmute.shopfee.collection.embedded.OrderEventEmbedded;
//import com.hcmute.shopfee.enums.OrderStatus;
//import com.hcmute.shopfee.model.elasticsearch.OrderIndex;
//import com.hcmute.shopfee.repository.elasticsearch.OrderSearchRepository;
//import com.hcmute.shopfee.service.database.implement.OrderItemService;
//import com.hcmute.shopfee.service.database.implement.ProductService;
//import com.hcmute.shopfee.service.database.implement.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class OrderSearchService {
//    private final UserService userService;
//    private final OrderSearchRepository orderSearchRepository;
//    private final OrderItemService orderItemService;
//    private final ProductService productService;
////    private final ElasticsearchTemplate elasticsearchTemplate;
//
//    public OrderIndex createOrder(OrderCollection orderCollection) {
//        UserCollection user = userService.getById(orderCollection.getUserId().toString());
//        List<OrderItemCollection> orderItemList = orderItemService.getOrderItemListByOrderId(orderCollection.getId());
//        ProductCollection product = productService.getById(orderItemList.get(0).getProductId().toString());
//        int lastIndexEvent = orderCollection.getEventList().size() - 1;
//        String recipientName = orderCollection.getRecipientInfo() != null ? orderCollection.getRecipientInfo().getRecipientName() : user.getFullName();
//        String phoneNumberReceiver = orderCollection.getRecipientInfo() != null ? orderCollection.getRecipientInfo().getPhoneNumber() : user.getPhoneNumber();
//        OrderIndex orderIndex = OrderIndex.builder()
//                .id(orderCollection.getId())
//                .code(orderCollection.getCode())
//                .customerName(user.getFullName())
//                .recipientName(recipientName)
//                .phoneNumber(user.getPhoneNumber())
//                .phoneNumberReceiver(phoneNumberReceiver)
//                .orderType(orderCollection.getOrderType())
//                .statusLastEvent(OrderStatus.CREATED)
//                .total(orderCollection.getTotal())
//                .productQuantity(orderItemList.size())
//                .productThumbnail(product.getThumbnailUrl())
//                .customerCode(user.getCode())
//                .timeLastEvent(orderCollection.getEventList().get(lastIndexEvent).getTime())
//                .createdAt(orderCollection.getCreatedAt())
//                .email(user.getEmail())
//                .build();
//        return orderSearchRepository.save(orderIndex);
//    }
//
//    public void upsertOrder(OrderCollection orderCollection) {
//        UserCollection user = userService.getById(orderCollection.getUserId().toString());
//        List<OrderItemCollection> orderItemList = orderItemService.getOrderItemListByOrderId(orderCollection.getId());
//        ProductCollection product = productService.getById(orderItemList.get(0).getProductId().toString());
//        OrderIndex order = orderSearchRepository.findById(orderCollection.getId()).orElse(null);
//        int lastIndexEvent = orderCollection.getEventList().size() - 1;
//        OrderEventEmbedded lastStatus = orderCollection.getEventList().get(lastIndexEvent);
//        if (order != null) {
//            order.setCustomerName(user.getFullName());
//            order.setRecipientName(orderCollection.getRecipientInfo().getRecipientName());
//            order.setPhoneNumber(orderCollection.getRecipientInfo().getPhoneNumber());
//            order.setOrderType(orderCollection.getOrderType());
//            order.setStatusLastEvent(lastStatus.getOrderStatus());
//            order.setTotal(orderCollection.getTotal());
//            order.setProductQuantity(orderItemList.size());
//            order.setProductThumbnail(product.getThumbnailUrl());
//            order.setCustomerCode(user.getCode());
//            order.setTimeLastEvent(lastStatus.getTime());
//            orderSearchRepository.save(order);
//        } else {
//            createOrder(orderCollection);
//        }
//    }
//
//
//    public Page<OrderIndex> searchOrderForAdmin(String key, int page, int size, String status) {
//        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
//        Pageable pageable = PageRequest.of(page - 1, size, sort);
//
//        return orderSearchRepository.searchOrderForAdmin(key, status, pageable);
//    }
//}
