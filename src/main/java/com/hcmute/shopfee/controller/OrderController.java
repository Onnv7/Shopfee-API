package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateOnsiteOrderRequest;
import com.hcmute.shopfee.dto.request.CreateShippingOrderRequest;
import com.hcmute.shopfee.dto.response.CreateOrderResponse;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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

}
