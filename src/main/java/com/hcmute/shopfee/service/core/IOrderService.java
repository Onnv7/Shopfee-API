package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.enums.OrderPhasesStatus;
import com.hcmute.shopfee.enums.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IOrderService {
    CreateOrderResponse createShippingOrder(CreateShippingOrderRequest body, HttpServletRequest request);
    CreateOrderResponse createOnsiteOrder(CreateOnsiteOrderRequest body, HttpServletRequest request);
    GetOrderHistoryForEmployeeResponse getOrderHistoryPageForEmployee(OrderStatus orderStatus, int page, int size, String key);
    void insertOrderEventByEmployee(String id, UpdateOrderStatusRequest body, HttpServletRequest request);
    void createCancellationRequest(CreateCancellationDemandRequest body, String orderId);
    void processCancellationRequest(ProcessCancellationDemandRequest body, String orderId);
    void cancelOrder(String orderId, CancelOrderBillRequest body) ;
    GetOrderQueueResponse getShippingOrderQueueToday(OrderStatus orderStatus, int page, int size);
    GetOrderQueueResponse getOnsiteOrderQueueToday(OrderStatus orderStatus, int page, int size);
    GetOrderListResponse getOrderListForAdmin(int page, int size, String key, OrderStatus status);
    GetOrderByIdResponse getOrderDetailsById(String id);
    List<GetOrderItemAndReviewResponse> getOrderItemAndReviewByOrderBillId(String orderBillId);
    GetShippingFeeResponse getShippingFee(Double lat, Double lng);
    List<GetAllOrderHistoryByUserIdResponse> getOrdersHistoryByUserId(String userId, OrderPhasesStatus orderPhasesStatus, int page, int size);
    List<GetOrderStatusLineResponse> getOrderEventLogById(String orderId);
    GetOrderQuantityByStatusResponse getOrderQuantityByStatusAtCurrentDate(OrderStatus orderStatus);
    GetCancellationByOrderBillIdRequest getCancellationRequestByOrderBillId(String orderBillId);
}
