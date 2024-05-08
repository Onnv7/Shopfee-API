package com.hcmute.shopfee.stateMachine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.module.ahamove.Ahamove;
import com.hcmute.shopfee.module.ahamove.order.*;
import com.hcmute.shopfee.statemachine.OrderEvent;
import com.hcmute.shopfee.statemachine.OrderStateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class OrderStateMachineConfigTest {

    @Autowired
    StateMachineFactory<OrderStatus, OrderEvent> factory;

    @Autowired
    Ahamove ahamove;

    @Autowired
    private OrderStateService orderStateService;
    @Test
    void testOrderStateMachine() {
        StateMachine<OrderStatus, OrderEvent> sm = factory.getStateMachine(UUID.randomUUID());
        sm.start();
        System.out.println("=>>>>>>> "+ sm.getState().toString());

        sm.sendEvent(OrderEvent.ACCEPT_ORDER);
        System.out.println("=>>>>>>> "+ sm.getState().toString());

        sm.sendEvent(OrderEvent.PREPARED);
        System.out.println("=>>>>>>> "+ sm.getState().toString());

        // =================================================================

//        sm.sendEvent(OrderEvent.START_SHIPPING);
//        System.out.println("=>>>>>>> "+ sm.getState().toString());
//
//        sm.sendEvent(OrderEvent.ORDER_FULFILL);
//        System.out.println("=>>>>>>> "+ sm.getState().toString());

        //  ===========================================================

        sm.sendEvent(OrderEvent.FULFILL);
        System.out.println("=>>>>>>> "+ sm.getState().toString());
    }

    @Test
    void testStateService() throws JsonProcessingException {


        DestinationPoint destinationPoint = new DestinationPoint();
        destinationPoint.setLat(10.7828887);
        destinationPoint.setLng(106.704898);
        destinationPoint.setAddress("Miss Ao Dai Building, 21 Nguyễn Trung Ngạn, Bến Nghé, Quận 1, Hồ Chí Minh, Vietnam");
        destinationPoint.setName("Bao");
        destinationPoint.setMobile("84931245678");
        destinationPoint.setRemarks("call me");
        destinationPoint.setCod(1000L);
        destinationPoint.setTrackingNumber("3234");

        StartingPoint startPoint = new StartingPoint();
        startPoint.setLat(10.7828887);
        startPoint.setLng(106.704898);
        startPoint.setAddress("Miss Ao Dai Building, 21 Nguyễn Trung Ngạn, Bến Nghé, Quận 1, Hồ Chí Minh, Vietnam");
        startPoint.setName("Bao");
        startPoint.setMobile("84931245678");
        startPoint.setShortAddress("Quan 10");
        OrderItem item = new OrderItem();
        item.setId("1");
        item.setName("banh");
        item.setNum(2);
        item.setPrice(23123);

        OrderItem item2 = new OrderItem();
        item2.setId("2");
        item2.setName("banh2");
        item2.setNum(3);
        item2.setPrice(3333);

        List<OrderItem> items = new ArrayList<OrderItem>();
        items.add(item);
        items.add(item2);
        ahamove.createOrder(startPoint, destinationPoint, items, PayMethod.CASH);
    }

}