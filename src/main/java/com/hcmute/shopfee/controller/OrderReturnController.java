package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.response.CreateOrderResponse;
import com.hcmute.shopfee.dto.request.CreateOrderReturnRequest;
import com.hcmute.shopfee.enums.AnswerStatus;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IOrderReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = ORDER_REFUND_CONTROLLER_TITLE)
@RestController
@RequiredArgsConstructor
@RequestMapping(ORDER_REFUND_BASE_PATH)
public class OrderReturnController {
    private final IOrderReturnService orderReturnService;
    @Operation(summary = ORDER_RETURN_CREATE_REQUEST_SUM)
    @PostMapping(path = POST_ORDER_REFUND_CREATE_REQUEST_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<?>> createOrderRefundRequest(@ModelAttribute @Valid CreateOrderReturnRequest body, @PathVariable(ORDER_ID) String orderId) {
        orderReturnService.createOrderRefundRequest(body, orderId);
        ResponseAPI<CreateOrderResponse> res = ResponseAPI.<CreateOrderResponse>builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = ORDER_RETURN_PROCESSING_REQUEST_SUM)
    @PatchMapping(path = PATCH_ORDER_REFUND_PROCESSING_REQUEST_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> processOrderRefundRequest(@RequestParam("status") AnswerStatus status, @PathVariable(ORDER_ID) String orderId) {
        orderReturnService.processOrderRefundRequest(status, orderId);
        ResponseAPI<CreateOrderResponse> res = ResponseAPI.<CreateOrderResponse>builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }
}
