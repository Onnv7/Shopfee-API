package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.CreateBuyXGetYCouponRequest;
import com.hcmute.shopfee.dto.request.CreateOrderCouponRequest;
import com.hcmute.shopfee.dto.request.CreateProductMoneyCouponRequest;
import com.hcmute.shopfee.dto.request.CreateShippingCouponRequest;
import com.hcmute.shopfee.dto.response.*;

import java.util.List;

public interface ICouponService {
    void createShippingCoupon(CreateShippingCouponRequest body);
    void createOrderCoupon(CreateOrderCouponRequest body);
    void createAmountOffProductCoupon(CreateProductMoneyCouponRequest body);
    void createGiftProductCoupon(CreateBuyXGetYCouponRequest body);
    void deleteCoupon(String couponId);
    List<GetReleaseCouponListResponse> getReleaseCouponList();
    GetReleaseCouponByIdResponse getReleaseCouponById(String couponId);
    List<GetCouponListResponse> getCouponList();
    GetShippingCouponDetailsByIdResponse getShippingCouponDetailById(String couponId);
    GetOrderCouponDetailByIdResponse getOrderCouponDetailById(String couponId);
    GetProductGiftCouponDetailByIdResponse getProductGiftCouponDetailById(String couponId);
    GetAmountOffProductCouponDetailByIdResponse getAmountOffProductCouponDetailById(String couponId);
}
