package com.hcmute.shopfee.stateMachine;

import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.statemachine.OrderEvent;
import com.hcmute.shopfee.statemachine.OrderStateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class OrderStateMachineConfigTest {

    @Autowired
    StateMachineFactory<OrderStatus, OrderEvent> factory;

    @Autowired
    private OrderStateService orderStateService;
    @Test
    void testOrderStateMachine() {
        StateMachine<OrderStatus, OrderEvent> sm = factory.getStateMachine(UUID.randomUUID());
        sm.start();
        System.out.println("=>>>>>>> "+ sm.getState().toString());

        sm.sendEvent(OrderEvent.ORDER_ACCEPT);
        System.out.println("=>>>>>>> "+ sm.getState().toString());

        sm.sendEvent(OrderEvent.READY_SHIPPING);
        System.out.println("=>>>>>>> "+ sm.getState().toString());

        // =================================================================

//        sm.sendEvent(OrderEvent.START_SHIPPING);
//        System.out.println("=>>>>>>> "+ sm.getState().toString());
//
//        sm.sendEvent(OrderEvent.ORDER_FULFILL);
//        System.out.println("=>>>>>>> "+ sm.getState().toString());

        //  ===========================================================

        sm.sendEvent(OrderEvent.ORDER_FULFILL);
        System.out.println("=>>>>>>> "+ sm.getState().toString());
    }

    @Test
    void testStateService() {
//        orderStateService.doEvent("OB01", O)
    }

}