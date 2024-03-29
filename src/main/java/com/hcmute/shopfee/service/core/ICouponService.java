package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

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
    List<GetReleaseCouponListResponse> getReleaseCouponList();
    GetReleaseCouponByIdResponse getReleaseCouponById(String couponId);
    List<GetCouponListResponse> getCouponList();
    GetShippingCouponDetailsByIdResponse getShippingCouponDetailById(String couponId);
    GetOrderCouponDetailByIdResponse getOrderCouponDetailById(String couponId);
    GetProductGiftCouponDetailByIdResponse getProductGiftCouponDetailById(String couponId);
    GetAmountOffProductCouponDetailByIdResponse getAmountOffProductCouponDetailById(String couponId);
    GetCouponListForCartResponse getCouponListForCartResponse(GetCouponListForCartRequest body);
}
