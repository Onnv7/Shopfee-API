package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateShippingCouponRequest;
import com.hcmute.shopfee.dto.response.GetReleaseCouponListResponse;
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
import java.util.List;

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

    @Operation(summary = COUPON_DELETE_BY_ID_SUM)
    @PostMapping(path = DELETE_COUPON_BY_ID_PATH)
    public ResponseEntity<ResponseAPI> deleteCouponById(@PathVariable(COUPON_ID) String couponId) {
        couponService.deleteCoupon(couponId);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = COUPON_GET_RELEASE_LIST_SUM)
    @GetMapping(path = GET_COUPON_RELEASE_LIST_SUB_PATH)
    public ResponseEntity<ResponseAPI> getReleaseCouponList() {
        List<GetReleaseCouponListResponse> resData = couponService.getReleaseCouponList();

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

}
