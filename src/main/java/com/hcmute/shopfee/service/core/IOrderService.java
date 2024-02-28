package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.CreateOnsiteOrderRequest;
import com.hcmute.shopfee.dto.request.CreateShippingOrderRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.enums.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

public interface IOrderService {
    CreateOrderResponse createShippingOrder(CreateShippingOrderRequest body, HttpServletRequest request);
    CreateOrderResponse createOnsiteOrder(CreateOnsiteOrderRequest body, HttpServletRequest request);
    List<GetOrderHistoryForEmployeeResponse> getOrderHistoryPageForEmployee(OrderStatus orderStatus, int page, int size, String key);
    void addNewOrderEvent(String id, OrderStatus orderStatus, String description, HttpServletRequest request) ;
    List<GetShippingOrderQueueResponse> getShippingOrderQueueToday(OrderStatus orderStatus, int page, int size);
    List<GetOnsiteOrderQueueResponse> getOnsiteOrderQueueToday(OrderStatus orderStatus, int page, int size);
    GetOrderListResponse getOrderListForAdmin(int page, int size, String key, OrderStatus status);
    GetOrderByIdResponse getOrderDetailsById(String id);
    List<GetAllOrderHistoryByUserIdResponse> getOrdersHistoryByUserId(String userId, OrderStatus orderStatus, int page, int size);
    List<GetOrderStatusLineResponse> getOrderEventLogById(String orderId);
    GetOrderQuantityByStatusResponse getOrderQuantityByStatusAtCurrentDate(OrderStatus orderStatus);
}
