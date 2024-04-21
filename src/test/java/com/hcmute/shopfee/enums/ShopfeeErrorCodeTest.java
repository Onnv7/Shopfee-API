package com.hcmute.shopfee.enums;

import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShopfeeErrorCodeTest {
    @Test
    public void testEnum() {
        System.out.println(ShopfeeErrorCode.USER_NOT_FOUND);
        ShopfeeErrorCode.USER_NOT_FOUND.description();
    }
}