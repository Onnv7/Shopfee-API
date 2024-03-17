package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.enums.OrderStaging;
import com.hcmute.shopfee.enums.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IOrderService {
    CreateOrderResponse createShippingOrder(CreateShippingOrderRequest body, HttpServletRequest request);
    CreateOrderResponse createOnsiteOrder(CreateOnsiteOrderRequest body, HttpServletRequest request);
    List<GetOrderHistoryForEmployeeResponse> getOrderHistoryPageForEmployee(OrderStatus orderStatus, int page, int size, String key);
    void insertOrderEventByEmployee(String id, UpdateOrderStatusRequest body, HttpServletRequest request);
    void createCancellationRequest(CreateCancellationDemandRequest body, String orderId);
    void processCancellationRequest(ProcessCancellationDemandRequest body, String orderId);
    void cancelOrder(String orderId, CancelOrderBillRequest body) ;
    List<GetShippingOrderQueueResponse> getShippingOrderQueueToday(OrderStatus orderStatus, int page, int size);
    List<GetOnsiteOrderQueueResponse> getOnsiteOrderQueueToday(OrderStatus orderStatus, int page, int size);
    GetOrderListResponse getOrderListForAdmin(int page, int size, String key, OrderStatus status);
    GetOrderByIdResponse getOrderDetailsById(String id);
    GetShippingFeeResponse getShippingFee(Double lat, Double lng);
    List<GetAllOrderHistoryByUserIdResponse> getOrdersHistoryByUserId(String userId, OrderStaging orderStaging, int page, int size);
    List<GetOrderStatusLineResponse> getOrderEventLogById(String orderId);
    GetOrderQuantityByStatusResponse getOrderQuantityByStatusAtCurrentDate(OrderStatus orderStatus);
}
