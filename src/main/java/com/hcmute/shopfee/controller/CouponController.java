package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateBuyXGetYCouponRequest;
import com.hcmute.shopfee.dto.request.CreateOrderCouponRequest;
import com.hcmute.shopfee.dto.request.CreateProductMoneyCouponRequest;
import com.hcmute.shopfee.dto.request.CreateShippingCouponRequest;
import com.hcmute.shopfee.dto.response.*;
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
    @Operation(summary = COUPON_CREATE_SHIPPING_TYPE_SUM)
    @PostMapping(path = POST_COUPON_CREATE_SHIPPING_TYPE_SUB_PATH)
    public ResponseEntity<ResponseAPI> createShippingCoupon(@RequestBody @Valid CreateShippingCouponRequest body) {
        couponService.createShippingCoupon(body);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = COUPON_CREATE_ORDER_TYPE_SUM)
    @PostMapping(path = POST_COUPON_CREATE_ORDER_TYPE_SUB_PATH)
    public ResponseEntity<ResponseAPI> createOrderCoupon(@RequestBody @Valid CreateOrderCouponRequest body) {
        couponService.createOrderCoupon(body);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = COUPON_CREATE_AMOUNT_OFF_PRODUCT_TYPE_SUM)
    @PostMapping(path = POST_COUPON_CREATE_AMOUNT_OFF_PRODUCT_TYPE_SUB_PATH)
    public ResponseEntity<ResponseAPI> createAmountOffProductCoupon(@RequestBody @Valid CreateProductMoneyCouponRequest body) {
        couponService.createAmountOffProductCoupon(body);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = COUPON_CREATE_BUY_GET_PRODUCT_GIFT_SUM)
    @PostMapping(path = POST_COUPON_CREATE_BUY_GET_TYPE_SUB_PATH)
    public ResponseEntity<ResponseAPI> createGiftProductCoupon(@RequestBody @Valid CreateBuyXGetYCouponRequest body) {
        couponService.createGiftProductCoupon(body);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = COUPON_DELETE_BY_ID_SUM)
    @DeleteMapping(path = DELETE_COUPON_BY_ID_SUB_PATH)
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
    @Operation(summary = COUPON_GET_RELEASE_BY_ID_SUM)
    @GetMapping(path = GET_COUPON_RELEASE_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI> getReleaseCouponById(@PathVariable(COUPON_ID) String couponId) {
        GetReleaseCouponByIdResponse resData = couponService.getReleaseCouponById(couponId);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = COUPON_GET_LIST_SUM)
    @GetMapping(path = GET_COUPON_LIST_SUB_PATH)
    public ResponseEntity<ResponseAPI> getCouponList() {
        List<GetCouponListResponse> resData = couponService.getCouponList();

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = COUPON_GET_SHIPPING_BY_ID_SUM)
    @GetMapping(path = GET_COUPON_SHIPPING_DETAIL_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI> getShippingCouponDetailById(@PathVariable(COUPON_ID) String couponId) {
        GetShippingCouponDetailsByIdResponse resData = couponService.getShippingCouponDetailById(couponId);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = COUPON_GET_ORDER_BY_ID_SUM)
    @GetMapping(path = GET_COUPON_ORDER_DETAIL_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI> getOrderCouponDetailById(@PathVariable(COUPON_ID) String couponId) {
        GetOrderCouponDetailByIdResponse resData = couponService.getOrderCouponDetailById(couponId);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = COUPON_GET_PRODUCT_GIFT_BY_ID_SUM)
    @GetMapping(path = GET_COUPON_PRODUCT_GIFT_DETAIL_BY_ID_PATH)
    public ResponseEntity<ResponseAPI> getProductGiftCouponDetailById(@PathVariable(COUPON_ID) String couponId) {
        GetProductGiftCouponDetailByIdResponse resData = couponService.getProductGiftCouponDetailById(couponId);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = COUPON_GET_AMOUNT_OFF_PRODUCT_BY_ID_SUM)
    @GetMapping(path = GET_COUPON_AMOUNT_OFF_PRODUCT_DETAIL_BY_ID_PATH)
    public ResponseEntity<ResponseAPI> getAmountOffProductCouponDetailById(@PathVariable(COUPON_ID) String couponId) {
        GetAmountOffProductCouponDetailByIdResponse resData = couponService.getAmountOffProductCouponDetailById(couponId);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
