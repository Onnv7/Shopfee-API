package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.SecurityConstant;
import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.response.CreateOrderResponse;
import com.hcmute.shopfee.dto.request.CreateOrderReturnRequest;
import com.hcmute.shopfee.dto.response.GetOrderRefundResponse;
import com.hcmute.shopfee.enums.AnswerStatus;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IOrderRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = ORDER_REFUND_CONTROLLER_TITLE)
@RestController
@RequiredArgsConstructor
@RequestMapping(ORDER_REFUND_BASE_PATH)
public class OrderReturnController {
    private final IOrderRefundService orderRefundService;
    @Operation(summary = ORDER_RETURN_CREATE_REQUEST_SUM)
    @PostMapping(path = POST_ORDER_REFUND_CREATE_REQUEST_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(SecurityConstant.ROLE_USER)
    public ResponseEntity<ResponseAPI<?>> createOrderRefundRequest(@ModelAttribute @Valid CreateOrderReturnRequest body, @PathVariable(ORDER_ID) String orderId) {
        orderRefundService.createOrderRefundRequest(body, orderId);
        ResponseAPI<CreateOrderResponse> res = ResponseAPI.<CreateOrderResponse>builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = ORDER_RETURN_PROCESSING_REQUEST_SUM)
    @PatchMapping(path = PATCH_ORDER_REFUND_PROCESSING_REQUEST_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_WAITER)
    public ResponseEntity<ResponseAPI<?>> processOrderRefundRequest(@RequestParam("status") AnswerStatus status, @PathVariable(ORDER_ID) String orderId) {
        orderRefundService.processOrderRefundRequest(status, orderId);
        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ORDER_RETURN_GET_REQUEST_SUM)
    @GetMapping(path = GET_ORDER_REFUND_REQUEST_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_WAITER_USER)
    public ResponseEntity<ResponseAPI<GetOrderRefundResponse>> getOrderRefundRequest(@PathVariable(ORDER_ID) String orderId) {
        GetOrderRefundResponse data = orderRefundService.getOrderRefundRequest(orderId);
        ResponseAPI<GetOrderRefundResponse> res = ResponseAPI.<GetOrderRefundResponse>builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
