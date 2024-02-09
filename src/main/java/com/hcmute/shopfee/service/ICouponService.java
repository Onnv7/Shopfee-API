package com.hcmute.shopfee.service;

import com.hcmute.shopfee.dto.request.CreateOrderCouponRequest;
import com.hcmute.shopfee.dto.request.CreateShippingCouponRequest;
import com.hcmute.shopfee.dto.response.GetCouponDetailsByIdResponse;
import com.hcmute.shopfee.dto.response.GetCouponListResponse;
import com.hcmute.shopfee.dto.response.GetReleaseCouponByIdResponse;
import com.hcmute.shopfee.dto.response.GetReleaseCouponListResponse;
import com.hcmute.shopfee.enums.CouponType;

import java.util.List;

public interface ICouponService {
    void createShippingCoupon(CreateShippingCouponRequest body);
    void createOrderCoupon(CreateOrderCouponRequest body);
    void deleteCoupon(String couponId);
    List<GetReleaseCouponListResponse> getReleaseCouponList();
    GetReleaseCouponByIdResponse getReleaseCouponById(String couponId);
    List<GetCouponListResponse> getCouponList();
    GetCouponDetailsByIdResponse getCouponById(String couponId);
}
