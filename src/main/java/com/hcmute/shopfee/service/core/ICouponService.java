package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.enums.CouponType;
import com.hcmute.shopfee.payload.request.*;
import com.hcmute.shopfee.payload.response.*;

import java.util.List;

public interface ICouponService {
    void createShippingCoupon(CreateShippingCouponRequest body);
    void updateShippingCoupon(UpdateShippingCouponRequest body, String couponId);
    void createOrderCoupon(CreateOrderCouponRequest body);
    void updateOrderCoupon(UpdateOrderCouponRequest body, String couponId);
    void createAmountOffProductCoupon(CreateProductMoneyCouponRequest body);
    void updateAmountOffProductCoupon(UpdateProductMoneyCouponRequest body, String couponId);
    void createGiftProductCoupon(CreateBuyXGetYCouponRequest body);
    void updateGiftProductCoupon(UpdateBuyXGetYCouponRequest body, String couponId);
    void deleteCoupon(String couponId);
    List<GetReleaseCouponListResponse> getReleaseCouponList(int quantity, CouponType type);
    GetReleaseCouponByIdResponse getReleaseCouponById(String couponId);
    List<GetCouponListResponse> getCouponList();
    GetShippingCouponDetailsByIdResponse getShippingCouponDetailById(String couponId);
    GetOrderCouponDetailByIdResponse getOrderCouponDetailById(String couponId);
    GetProductGiftCouponDetailByIdResponse getProductGiftCouponDetailById(String couponId);
    GetAmountOffProductCouponDetailByIdResponse getAmountOffProductCouponDetailById(String couponId);
    GetCouponOptionsResponse getCouponListForCartResponse(GetCouponListForCartRequest body);
    List<CheckCouponInCartResponse> validateCouponAndItemInCart(GetCouponListForCartRequest body);
}
