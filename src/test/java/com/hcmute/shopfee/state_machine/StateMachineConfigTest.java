package com.hcmute.shopfee.state_machine;

import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.state_machine.domain.OrderEvent;
import com.hcmute.shopfee.state_machine.domain.PaymentEvent;
import com.hcmute.shopfee.state_machine.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest

class StateMachineConfigTest {
    @Autowired
    StateMachineFactory<OrderStatus, OrderEvent> factory;

    @Test
    void testStateMachine() {
        StateMachine<OrderStatus, OrderEvent> sm = factory.getStateMachine(UUID.randomUUID());

        sm.start();
        System.out.println("ANNN"+ sm.getState().toString());

        sm.sendEvent(OrderEvent.FULFILL);
        System.out.println(sm.getState().toString());
    }

}