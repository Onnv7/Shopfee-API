package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.response.GetRevenueByTimeResponse;
import com.hcmute.shopfee.dto.response.GetRevenueCurrentDateResponse;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ITransactionService {
    void updateTransaction(String id, HttpServletRequest request);

    //    void completeTransaction(String transId);
    void refundOrder(OrderBillEntity orderBill, boolean refundCoin, boolean refundMoney);
}
