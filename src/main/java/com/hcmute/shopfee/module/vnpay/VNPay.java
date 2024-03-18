package com.hcmute.shopfee.module.vnpay;

import com.hcmute.shopfee.module.vnpay.querydr.QueryDr;
import com.hcmute.shopfee.module.vnpay.transaction.dto.PreTransactionInfo;
import com.hcmute.shopfee.module.vnpay.transaction.VNPayTransaction;
import com.hcmute.shopfee.module.vnpay.querydr.response.TransactionInfoQuery;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class VNPay {
    private final String SECRET_KEY;
    private final String TMN_CODE;
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "http://localhost:8080/vnpay_jsp/vnpay_return.jsp";
    public static String vnp_Version = "2.1.0";
    public static String vnp_Command = "pay";
    public static String orderType = "other";
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";


    private final VNPayTransaction vnPayTransaction;
    private final QueryDr queryDr;
    public String getSecretKey() {
        return SECRET_KEY;
    }

    public String getTmnCode() {
        return TMN_CODE;
    }

    public VNPay(String secretKey, String tmnCode) {
        SECRET_KEY = secretKey;
        TMN_CODE = tmnCode;
        vnPayTransaction = new VNPayTransaction(this);
        queryDr = new QueryDr(this);
    }

    public PreTransactionInfo createUrlPayment(HttpServletRequest request, long amount, String orderInfo) throws UnsupportedEncodingException {
        return vnPayTransaction.createUrlPayment(request, amount, orderInfo);
    }

    public TransactionInfoQuery getTransactionInfo(String txnref, String transId, String ipAddress) throws IOException {
        return queryDr.getTransactionInfo(txnref, transId, ipAddress);
    }

    public TransactionInfoQuery getTransactionInfoTest(String txnref, String transId, String request) throws IOException {
        return queryDr.getTransactionInfoTest(txnref, transId, request);
    }

    public String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
    public String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }
    public String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }
}
