package com.hcmute.shopfee.statemachine;

import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.enums.TransactionStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderTypeValidationGuard {

    @Autowired
    private TransactionRepository transactionRepository;
//    public OrderTypeValidationGuard() {
//    }

    public Guard<OrderStatus, OrderEvent> validateOrderType(OrderType orderType) {
        return context -> {
            OrderType orderTypeMsg = OrderType.valueOf(context.getMessage().getHeaders().getOrDefault(OrderStateService.ORDER_TYPE_HEADER, "").toString());

            if(orderTypeMsg == orderType) {
                return true;
            }
            context.getStateMachine().setStateMachineError(new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY, "Guard"));
            return false;
        };
    }

    public Guard<OrderStatus, OrderEvent> validateBankingPayment() {
        return context -> {
            PaymentType paymentType = PaymentType.valueOf(context.getMessage().getHeaders().getOrDefault(OrderStateService.PAYMENT_TYPE_HEADER, "").toString());

            if(paymentType == PaymentType.CASHING) {
                context.getStateMachine().setStateMachineError(new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY, "Guard"));
                return false;
            }
            return true;
        };
    }

    public Guard<OrderStatus, OrderEvent> validateTransaction() {
        return context -> {
            PaymentType paymentType = PaymentType.valueOf(context.getMessage().getHeaders().getOrDefault(OrderStateService.PAYMENT_TYPE_HEADER, "").toString());
            TransactionStatus transactionStatus = TransactionStatus.valueOf(context.getMessage().getHeaders().getOrDefault(OrderStateService.TRANSACTION_STATUS_HEADER, "").toString());

            if(paymentType != PaymentType.CASHING && transactionStatus != TransactionStatus.PAID) {
                context.getStateMachine().setStateMachineError(new ShopfeeException(ShopfeeErrorCode.SupErrorCode.ACTING_INCORRECTLY, "Guard"));
                return false;
            }
            return true;
        };
    }
}