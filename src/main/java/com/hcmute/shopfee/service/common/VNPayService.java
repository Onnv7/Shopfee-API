package com.hcmute.shopfee.service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.shopfee.module.vnpay.VNPay;
import com.hcmute.shopfee.module.vnpay.VNPayUtils;
import com.hcmute.shopfee.dto.common.vnpay.VNPayPaymentUrl;
import com.hcmute.shopfee.dto.common.vnpay.TransactionInfoQuery;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import com.hcmute.shopfee.repository.database.payment.VNPayRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayService {
    private final VNPay vnPay;
    private final VNPayRepository vnpayRepository;
    private final OrderBillRepository orderBillRepository;
    private final TransactionRepository transactionRepository;

    public VNPayPaymentUrl createUrlPayment(HttpServletRequest request, long amount, String orderInfo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object>  responseUrl = vnPay.createUrlPayment(request, amount, orderInfo);
            return objectMapper.convertValue(responseUrl, VNPayPaymentUrl.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TransactionInfoQuery getTransactionInfo(String invoiceCode, String timeCode, HttpServletRequest request) {
        try {
            String ipAddress = "";
            if (request == null) {
                ipAddress = "127.0.0.1";
            } else {
                ipAddress = VNPayUtils.getIpAddress(request);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> transactionInfo = vnPay.getTransactionInfo(invoiceCode, timeCode, ipAddress);
            return objectMapper.convertValue(transactionInfo, TransactionInfoQuery.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> refundOrder(HttpServletRequest request, String timeCode, String invoiceCode,  long amount) throws IOException {
        return vnPay.refund(request, timeCode, invoiceCode, amount);
    }


}
