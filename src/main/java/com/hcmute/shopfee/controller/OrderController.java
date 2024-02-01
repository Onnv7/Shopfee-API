package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateOnsiteOrderRequest;
import com.hcmute.shopfee.dto.request.CreateShippingOrderRequest;
import com.hcmute.shopfee.dto.request.UpdateOrderStatusRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class OrderController {
    private final IOrderService orderService;

    @Operation(summary = ORDER_CREATE_SHIPPING_SUM)
    @PostMapping(path = POST_ORDER_CREATE_SHIPPING_SUB_PATH)
    public ResponseEntity<ResponseAPI> createShippingOrder(HttpServletRequest request, @RequestBody @Valid CreateShippingOrderRequest body) {
        CreateOrderResponse resData = orderService.createShippingOrder(body, request);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = ORDER_CREATE_ONSITE_SUM)
    @PostMapping(path = POST_ORDER_CREATE_ONSITE_SUB_PATH)
    public ResponseEntity<ResponseAPI> createOnsiteOrder(HttpServletRequest request, @RequestBody @Valid CreateOnsiteOrderRequest body) {
        CreateOrderResponse resData = orderService.createOnsiteOrder(body, request);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }
    @Operation(summary = ORDER_GET_ALL_ORDER_HISTORY_FOR_EMPLOYEE_SUM)
    @GetMapping(path = GET_ORDER_ALL_ORDER_HISTORY_FOR_EMPLOYEE_SUB_PATH)
    public ResponseEntity<ResponseAPI> getOrderHistoryPageForEmployee(
            @PathVariable("orderStatus") OrderStatus orderStatus,
            @Parameter(name = "key", description = "Key is order's code, customerCode, email, phoneNumber, phoneNumberReceiver", required = false, example = "U00000001")
            @RequestParam(name = "key", required = false) String key,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size) {
        List<GetOrderHistoryForEmployeeResponse> resData = new ArrayList<>();
        resData = orderService.getOrderHistoryPageForEmployee(orderStatus, page, size, key);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);

    }
    @Operation(summary = ORDER_UPDATE_EVENT_SUM)
    @PatchMapping(path = PATCH_ORDER_UPDATE_STATUS_SUB_PATH)
    public ResponseEntity<ResponseAPI> addNewOrderEvent(
            @PathVariable(ORDER_ID) String id,
            @RequestBody @Valid UpdateOrderStatusRequest body) {

        orderService.addNewOrderEvent(id, body.getOrderStatus(), body.getDescription());

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = ORDER_GET_ALL_BY_TYPE_AND_STATUS_IN_DAY_SUM)
    @GetMapping(path = GET_ORDER_ALL_IN_QUEUE_SUB_PATH)
    public ResponseEntity<ResponseAPI> getOrderQueueToday(
            @RequestParam("orderType") OrderType orderType,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,
            @RequestParam("orderStatus") OrderStatus orderStatus
    ) {

        List<?> dataRes = new ArrayList<>();
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
    public ResponseEntity<ResponseAPI> getOrderListForAdmin(
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
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_DETAILS_BY_ID_SUM)
    @GetMapping(path = GET_ORDER_DETAILS_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI> getOrderDetailsById(@PathVariable(ORDER_ID) String id) {
        GetOrderByIdResponse savedData = orderService.getOrderDetailsById(id);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(savedData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_ORDERS_BY_USER_ID_AND_ORDER_STATUS_SUM)
    @GetMapping(path = GET_ORDER_ORDERS_BY_USER_ID_AND_ORDER_STATUS_SUB_PATH)
    public ResponseEntity<ResponseAPI> getOrdersHistoryByUserId(
            @PathVariable("userId") String id,
            @RequestParam("orderStatus") OrderStatus orderStatus,
            @Parameter(name = "page", required = true, example = "1")
            @Min(value = 1, message = "Page must be greater than 0")
            @RequestParam("page") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size
    ) {
        List<GetAllOrderHistoryByUserIdResponse> savedData = orderService.getOrdersHistoryByUserId(id, orderStatus, page, size);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(savedData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_STATUS_LINE_SUM)
    @GetMapping(path = GET_ORDER_STATUS_LINE_SUB_PATH)
    public ResponseEntity<ResponseAPI> getOrderStatusLine(@PathVariable(ORDER_ID) String orderId) {
        List<GetOrderStatusLineResponse> resData = orderService.getOrderEventLogById(orderId);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_GET_ORDER_QUANTITY_BY_STATUS_SUM)
    @GetMapping(path = GET_ORDER_ORDER_QUANTITY_BY_STATUS_SUB_PATH)
    public ResponseEntity<ResponseAPI> getOrderQuantityByStatusAtCurrentDate(@RequestParam("status") OrderStatus orderStatus) {
        GetOrderQuantityByStatusResponse resData = orderService.getOrderQuantityByStatusAtCurrentDate(orderStatus);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
