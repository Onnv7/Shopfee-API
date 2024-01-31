package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateShippingCouponRequest;
import com.hcmute.shopfee.enums.CouponType;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.ICouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = COUPON_CONTROLLER_TITLE)
@RestController
@RequestMapping(COUPON_BASE_PATH)
@RequiredArgsConstructor
public class CouponController {
    private final ICouponService couponService;
    @Operation(summary = COUPON_CREATE_MONEY_DISCOUNT_SUM)
    @PostMapping(path = POST_COUPON_CREATE_SUB_PATH)
    public ResponseEntity<ResponseAPI> createCoupon(@RequestBody @Valid CreateShippingCouponRequest body) {
        couponService.createShippingCoupon(body);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.CREATED);
    }
}
