package com.hcmute.shopfee.statemachine;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.model.ShopfeeException;
import org.springframework.statemachine.guard.Guard;

public class OrderTypeValidationGuard {

    public OrderTypeValidationGuard() {
    }

    public Guard<OrderStatus, OrderEvent> validateOrderType(OrderType orderType) {
        return context -> {
            OrderType orderTypeMsg = OrderType.valueOf(context.getMessage().getHeaders().getOrDefault(OrderStateService.ORDER_TYPE_HEADER, "").toString());

            if(orderTypeMsg == orderType) {
                return true;
            }
            context.getStateMachine().setStateMachineError(new ShopfeeException(ErrorConstant.ACTING_INCORRECTLY, "Guard"));
            return false;
        };
    }
}