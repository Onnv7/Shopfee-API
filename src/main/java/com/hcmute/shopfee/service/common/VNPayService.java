package com.hcmute.shopfee.service.common;

import com.hcmute.shopfee.module.vnpay.VNPay;
import com.hcmute.shopfee.module.vnpay.transaction.dto.PreTransactionInfo;
import com.hcmute.shopfee.module.vnpay.querydr.response.TransactionInfoQuery;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class VNPayService {
    private final VNPay vnPay;

    public PreTransactionInfo createUrlPayment(HttpServletRequest request, long amount, String orderInfo) {
        try {
            return vnPay.createUrlPayment(request, amount, orderInfo);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public TransactionInfoQuery getTransactionInfoTest(String txnref, String transId, HttpServletRequest request) {
        try {
            return vnPay.getTransactionInfo(txnref, transId, request);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
