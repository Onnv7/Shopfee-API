package com.hcmute.shopfee.module.vnpay.transaction;

import com.hcmute.shopfee.module.vnpay.VNPay;
import com.hcmute.shopfee.module.vnpay.transaction.dto.PreTransactionInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hcmute.shopfee.constant.VNPayConstant.*;

public class VNPayTransaction {
    private final VNPay vnPay;

    public VNPayTransaction(VNPay vnPay) {
        this.vnPay = vnPay;
    }

    public PreTransactionInfo createUrlPayment(HttpServletRequest request, long amount, String orderInfo) throws UnsupportedEncodingException {
        String vnp_TxnRef = vnPay.getRandomNumber(8);
        String vnp_IpAddr = vnPay.getIpAddress(request);
        String vnp_TmnCode = vnPay.getTmnCode();

        PreTransactionInfo result = new PreTransactionInfo();
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put(VNP_VERSION_KEY, VNPay.vnp_Version);
        vnp_Params.put(VNP_COMMAND_KEY, VNPay.vnp_Command);
        vnp_Params.put(VNP_TMN_CODE_KEY, vnp_TmnCode);
        vnp_Params.put(VNP_AMOUNT_KEY, String.valueOf(amount * 100));
        vnp_Params.put(VNP_CURRENCY_CODE_KEY, "VND");
//        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put(VNP_TXN_REF_KEY, vnp_TxnRef);
        vnp_Params.put(VNP_ORDER_INFO_KEY, orderInfo);
        vnp_Params.put(VNP_RETURN_URL_KEY, "https://sandbox.vnpayment.vn/apis/docs/huong-dan-tich-hop/#code-returnurl");  //"http://localhost:8080/api/test/ok"
        vnp_Params.put(VNP_IP_ADDRESS_KEY, vnp_IpAddr);
        vnp_Params.put(VNP_ORDER_TYPE_KEY, "other");
        vnp_Params.put(VNP_LOCALE_KEY, "vn");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(VNP_TIME_ZONE));
        SimpleDateFormat formatter = new SimpleDateFormat(VNP_TIME_FORMAT);
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put(VNP_CREATE_DATE_KEY, vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put(VNP_EXPIRE_DATE_KEY, vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = vnPay.hmacSHA512(vnPay.getSecretKey(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPay.vnp_PayUrl + "?" + queryUrl;
        result.setVnpTxnRef(vnp_TxnRef);
        result.setVnpCreateDate(vnp_CreateDate);
        result.setVnpUrl(paymentUrl);
        return result;
    }



}
