package com.hcmute.shopfee.service;

import com.hcmute.shopfee.dto.request.CreateShippingCouponRequest;
import com.hcmute.shopfee.enums.CouponType;

public interface ICouponService {
    void createShippingCoupon(CreateShippingCouponRequest body);
}
