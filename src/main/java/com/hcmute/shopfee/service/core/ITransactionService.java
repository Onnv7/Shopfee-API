package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import jakarta.servlet.http.HttpServletRequest;

public interface ITransactionService {
    void updateTransaction(String id, HttpServletRequest request);
    void refundOrder(OrderBillEntity orderBill, boolean refundCoin, boolean refundMoney);
}
