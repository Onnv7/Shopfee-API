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

    public TransactionInfoQuery getTransactionInfo(String invoiceCode, String timeCode, HttpServletRequest request) {
        try {
            String ipAddress = "";
            if (request == null) {
                ipAddress = "127.0.0.1";
            } else {
                ipAddress = vnPay.getIpAddress(request);
            }
            return vnPay.getTransactionInfo(invoiceCode, timeCode, ipAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TransactionInfoQuery getTransactionInfoTest(String invoiceCode, String timeCode, String ip) {
        try {
            return vnPay.getTransactionInfoTest(invoiceCode, timeCode, ip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
