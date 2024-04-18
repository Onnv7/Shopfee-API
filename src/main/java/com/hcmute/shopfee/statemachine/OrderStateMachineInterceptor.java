package com.hcmute.shopfee.statemachine;


import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderStateMachineInterceptor extends StateMachineInterceptorAdapter<OrderStatus, OrderEvent> {

    private final OrderBillRepository orderBillRepository;


    @Override
    public Message<OrderEvent> preEvent(Message<OrderEvent> message, StateMachine<OrderStatus, OrderEvent> stateMachine) {
        return super.preEvent(message, stateMachine);
    }


    @Override
    public void preStateChange(State<OrderStatus, OrderEvent> state, Message<OrderEvent> message, Transition<OrderStatus, OrderEvent> transition, StateMachine<OrderStatus, OrderEvent> stateMachine, StateMachine<OrderStatus, OrderEvent> rootStateMachine) {
        super.preStateChange(state, message, transition, stateMachine, rootStateMachine);
    }

    @Override
    public void postStateChange(State<OrderStatus, OrderEvent> state, Message<OrderEvent> message, Transition<OrderStatus, OrderEvent> transition, StateMachine<OrderStatus, OrderEvent> stateMachine, StateMachine<OrderStatus, OrderEvent> rootStateMachine) {
        super.postStateChange(state, message, transition, stateMachine, rootStateMachine);
    }

    @Override
    public StateContext<OrderStatus, OrderEvent> preTransition(StateContext<OrderStatus, OrderEvent> stateContext) {
        return super.preTransition(stateContext);
    }

    @Override
    public StateContext<OrderStatus, OrderEvent> postTransition(StateContext<OrderStatus, OrderEvent> stateContext) {
        return super.postTransition(stateContext);
    }

}
