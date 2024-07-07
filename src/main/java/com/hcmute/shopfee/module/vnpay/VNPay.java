package com.hcmute.shopfee.module.vnpay;

import com.hcmute.shopfee.module.vnpay.querydr.VNPayQuery;
import com.hcmute.shopfee.module.vnpay.refund.VNPayRefund;
import com.hcmute.shopfee.module.vnpay.transaction.VNPayTransaction;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class VNPay {
    private final String SECRET_KEY;
    private final String TMN_CODE;
    public static String RETURN_URL;
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "http://localhost:8080/vnpay_jsp/vnpay_return.jsp";
    public static String vnp_Version = "2.1.0";
    public static String vnp_Command = "pay";
    public static String orderType = "other";
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";


    private final VNPayTransaction vnPayTransaction;
    private final VNPayQuery VNPayQuery;
    private final VNPayRefund vnpayRefund;

    public String getSecretKey() {
        return SECRET_KEY;
    }

    public String getTmnCode() {
        return TMN_CODE;
    }

    public VNPay(String secretKey, String tmnCode, String returnUrl) {
        RETURN_URL = returnUrl;
        SECRET_KEY = secretKey;
        TMN_CODE = tmnCode;
        vnPayTransaction = new VNPayTransaction(this);
        VNPayQuery = new VNPayQuery(this);
        vnpayRefund = new VNPayRefund(this);
    }

    public Map<String, Object> createUrlPayment(HttpServletRequest request, long amount, String orderInfo) throws UnsupportedEncodingException {
        return vnPayTransaction.createUrlPayment(request, amount, orderInfo);
    }

    public Map<String, Object> getTransactionInfo(String txnref, String transId, String ipAddress) throws IOException {
        return VNPayQuery.getTransactionInfo(txnref, transId, ipAddress);
    }


    public Map<String, Object> refund(HttpServletRequest req, String timeId, String invoiceCode, long amount) throws IOException {
        return vnpayRefund.refund(req, timeId, amount, invoiceCode, "02");
    }


}
