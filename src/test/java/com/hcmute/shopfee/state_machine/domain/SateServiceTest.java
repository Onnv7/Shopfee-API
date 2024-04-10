package com.hcmute.shopfee.state_machine.domain;

import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SateServiceTest {
    @Autowired
    SateService sateService;
    @Autowired
    OrderBillRepository orderBillRepository;


    @BeforeEach
    void setUp() {

    }

    @Test
    void cancel() {
        OrderBillEntity orderBill = orderBillRepository.findById("OB000000021")
                .orElseThrow(() -> new RuntimeException());
        sateService.cancel("OB000000021");

    }
}