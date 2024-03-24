package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.enums.OrderPhasesStatus;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = ORDER_CONTROLLER_TITLE)
@RestController
@RequiredArgsConstructor
@RequestMapping(ORDER_BASE_PATH)
public class OrderController {
    private final IOrderService orderService;

    @Operation(summary = ORDER_CREATE_SHIPPING_SUM)
    @PostMapping(path = POST_ORDER_CREATE_SHIPPING_SUB_PATH)
    public ResponseEntity<ResponseAPI<CreateOrderResponse>> createShippingOrder(HttpServletRequest request, @RequestBody @Valid CreateShippingOrderRequest body) {
        CreateOrderResponse resData = orderService.createShippingOrder(body, request);
        ResponseAPI<CreateOrderResponse> res = ResponseAPI.<CreateOrderResponse>builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = ORDER_CREATE_ONSITE_SUM)
    @PostMapping(path = POST_ORDER_CREATE_ONSITE_SUB_PATH)
    public ResponseEntity<ResponseAPI<CreateOrderResponse>> createOnsiteOrder(HttpServletRequest request, @RequestBody @Valid CreateOnsiteOrderRequest body) {
        CreateOrderResponse resData = orderService.createOnsiteOrder(body, request);
        ResponseAPI<CreateOrderResponse> res = ResponseAPI.<CreateOrderResponse>builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }
    @Operation(summary = ORDER_GET_ALL_ORDER_HISTORY_FOR_EMPLOYEE_SUM)
    @GetMapping(path = GET_ORDER_ALL_ORDER_HISTORY_FOR_EMPLOYEE_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetOrderHistoryForEmployeeResponse>> getOrderHistoryPageForEmployee(
            @PathVariable("orderStatus") OrderStatus orderStatus,
            @Parameter(name = "key", description = "Key is order's code, customerCode, email, phoneNumber, phoneNumberReceiver", required = false, example = "U00000001")
            @RequestParam(name = "key", required = false) String key,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size)
    {
        GetOrderHistoryForEmployeeResponse resData = orderService.getOrderHistoryPageForEmployee(orderStatus, page, size, key);

        ResponseAPI<GetOrderHistoryForEmployeeResponse> res = ResponseAPI.<GetOrderHistoryForEmployeeResponse>builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);

    }
    @Operation(summary = ORDER_UPDATE_EVENT_SUM)
    @PatchMapping(path = PATCH_ORDER_UPDATE_STATUS_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> insertOrderEventByEmployee(
            @PathVariable(ORDER_ID) String id,
            @RequestBody @Valid UpdateOrderStatusRequest body, HttpServletRequest request) {

        orderService.insertOrderEventByEmployee(id, body, request);

        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = ORDER_UPDATE_CANCEL_EVENT_SUM)
    @PatchMapping(path = PATCH_ORDER_UPDATE_CANCEL_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> cancelOrder(
            @PathVariable(ORDER_ID) String id,
            @RequestBody @Valid CancelOrderBillRequest body) {

        orderService.cancelOrder(id, body);

        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
    @Operation(summary = ORDER_CREATE_CANCELLATION_REQUEST_SUM)
    @PostMapping(path = POST_ORDER_CREATE_CANCELLATION_REQUEST_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> createCancellationRequest(
            @PathVariable(ORDER_ID) String orderId,
            @RequestBody @Valid CreateCancellationDemandRequest body) {

        orderService.createCancellationRequest(body, orderId);

        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = ORDER_UPDATE_CANCELLATION_REQUEST_SUM)
    @PatchMapping(path = PATCH_ORDER_UPDATE_CANCELLATION_DEMAND_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> processCancellationRequest(
            @PathVariable(ORDER_ID) String orderId,
            @RequestBody @Valid ProcessCancellationDemandRequest body) {

        orderService.processCancellationRequest(body, orderId);

        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_ALL_BY_TYPE_AND_STATUS_IN_DAY_SUM)
    @GetMapping(path = GET_ORDER_ALL_IN_QUEUE_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetOrderQueueResponse>> getOrderQueueToday(
            @RequestParam("orderType") OrderType orderType,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,
            @RequestParam("orderStatus") OrderStatus orderStatus
    ) {
        GetOrderQueueResponse dataRes = new GetOrderQueueResponse();
        if(orderType == OrderType.ONSITE) {
            dataRes = orderService.getOnsiteOrderQueueToday(orderStatus, page, size);
        } else if(orderType == OrderType.SHIPPING) {
            dataRes = orderService.getShippingOrderQueueToday(orderStatus, page, size);
        }
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(dataRes)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_LIST_SUM)
    @GetMapping(path = GET_ORDER_LIST_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetOrderListResponse>> getOrderListForAdmin(
            @Parameter(name = "key", description = "Key is order's code, customerCode, email, phoneNumber, phoneNumberReceiver", required = false, example = "an nguyen")
            @RequestParam(name = "key", required = false) String key,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,
            @Parameter(name = "status")
            @RequestParam(name = "status", required = false) OrderStatus status
    ) {
        GetOrderListResponse resData = orderService.getOrderListForAdmin(page, size, key, status);
        ResponseAPI<GetOrderListResponse> res = ResponseAPI.<GetOrderListResponse>builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_DETAILS_BY_ID_SUM)
    @GetMapping(path = GET_ORDER_DETAILS_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetOrderByIdResponse>> getOrderDetailsById(@PathVariable(ORDER_ID) String id) {
        GetOrderByIdResponse resData = orderService.getOrderDetailsById(id);
        ResponseAPI<GetOrderByIdResponse> res = ResponseAPI.<GetOrderByIdResponse>builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_ORDER_ITEM_REVIEW_SUM)
    @GetMapping(path = GET_ORDER_ITEM_REVIEW_SUB_PATH)
    public ResponseEntity<ResponseAPI<List<GetOrderItemAndReviewResponse>>> getOrderItemAndReview(@PathVariable(ORDER_ID) String id) {
        List<GetOrderItemAndReviewResponse> resData = orderService.getOrderItemAndReviewByOrderBillId(id);
        ResponseAPI<List<GetOrderItemAndReviewResponse>> res = ResponseAPI.<List<GetOrderItemAndReviewResponse>>builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_SHIPPING_FEE_SUM)
    @GetMapping(path = GET_ORDER_SHIPPING_FEE_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetShippingFeeResponse>> getShippingFee(
            @Parameter(name = "lat", required = true, example = "10.8005397")
            @RequestParam("lat")  Double lat,
            @Parameter(name = "lng", required = true, example = "106.6393208")
            @RequestParam("lng")  Double lng
    ) {
        GetShippingFeeResponse resData = orderService.getShippingFee(lat, lng);
        ResponseAPI<GetShippingFeeResponse> res = ResponseAPI.<GetShippingFeeResponse>builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_ORDERS_BY_USER_ID_AND_ORDER_STATUS_SUM)
    @GetMapping(path = GET_ORDER_ORDERS_BY_USER_ID_AND_ORDER_STATUS_SUB_PATH)
    public ResponseEntity<ResponseAPI<List<GetAllOrderHistoryByUserIdResponse>>> getOrdersHistoryByUserId(
            @PathVariable("userId") String id,
            @RequestParam("order_phases_status") OrderPhasesStatus orderPhasesStatus,
            @Parameter(name = "page", required = true, example = "1")
            @Min(value = 1, message = "Page must be greater than 0")
            @RequestParam("page") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size
    ) {
        List<GetAllOrderHistoryByUserIdResponse> savedData = orderService.getOrdersHistoryByUserId(id, orderPhasesStatus, page, size);
        ResponseAPI<List<GetAllOrderHistoryByUserIdResponse>> res = ResponseAPI.<List<GetAllOrderHistoryByUserIdResponse>>builder()
                .timestamp(new Date())
                .data(savedData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_STATUS_LINE_SUM)
    @GetMapping(path = GET_ORDER_STATUS_LINE_SUB_PATH)
    public ResponseEntity<ResponseAPI<List<GetOrderStatusLineResponse>>> getOrderStatusLine(@PathVariable(ORDER_ID) String orderId) {
        List<GetOrderStatusLineResponse> resData = orderService.getOrderEventLogById(orderId);

        ResponseAPI<List<GetOrderStatusLineResponse>> res = ResponseAPI.<List<GetOrderStatusLineResponse>>builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_ORDER_QUANTITY_BY_STATUS_SUM)
    @GetMapping(path = GET_ORDER_ORDER_QUANTITY_BY_STATUS_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetOrderQuantityByStatusResponse>> getOrderQuantityByStatusAtCurrentDate(@RequestParam("status") OrderStatus orderStatus) {
        GetOrderQuantityByStatusResponse resData = orderService.getOrderQuantityByStatusAtCurrentDate(orderStatus);
        ResponseAPI<GetOrderQuantityByStatusResponse> res = ResponseAPI.<GetOrderQuantityByStatusResponse>builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
