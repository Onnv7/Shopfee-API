package com.hcmute.shopfee.statemachine;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

public class OrderTypeValidationGuard {

    public OrderTypeValidationGuard() {
    }

    public Guard<OrderStatus, OrderEvent> validateOrderType(OrderType orderType) {
        return context -> {
            OrderType orderTypeMsg = OrderType.valueOf(context.getMessage().getHeaders().getOrDefault(OrderStateService.ORDER_TYPE_HEADER, "").toString());

            if(orderTypeMsg == orderType) {
                return true;
            }
            context.getStateMachine().setStateMachineError(new CustomException(ErrorConstant.ACTING_INCORRECTLY, "Guard"));
            return false;
        };
    }
}