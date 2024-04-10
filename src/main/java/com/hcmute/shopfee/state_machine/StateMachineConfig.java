package com.hcmute.shopfee.state_machine;

import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.state_machine.domain.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<OrderStatus, OrderEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvent> states) throws Exception {
        states.withStates()
                .initial(OrderStatus.CREATED)
                .states(EnumSet.allOf(OrderStatus.class))
                .end(OrderStatus.NOT_RECEIVED)
                .end(OrderStatus.CANCELED)
                .end(OrderStatus.SUCCEED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvent> transitions) throws Exception {
        transitions.withExternal().source(OrderStatus.CREATED).target(OrderStatus.CANCELED).event(OrderEvent.CANCEL).action(preOrder())
                .and()
                .withExternal().source(OrderStatus.CREATED).target(OrderStatus.SUCCEED).event(OrderEvent.FULFILL)
                .and()
                .withExternal().source(OrderStatus.CREATED).target(OrderStatus.NOT_RECEIVED).event(OrderEvent.BOOM);

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatus, OrderEvent> config) throws Exception {
        StateMachineListenerAdapter<OrderStatus, OrderEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<OrderStatus, OrderEvent> from, State<OrderStatus, OrderEvent> to) {
                log.info(String.format("State changed from %s to %s", from, to));
            }
        };
        config.withConfiguration().listener(adapter);
    }

    public Action<OrderStatus, OrderEvent> preOrder() {
        return context -> {
            System.out.println("Pre called");

        };
    }
}
