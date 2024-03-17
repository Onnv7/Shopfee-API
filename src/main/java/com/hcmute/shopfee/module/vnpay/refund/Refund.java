package com.hcmute.shopfee.module.vnpay.refund;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.hcmute.shopfee.module.vnpay.VNPay;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import static com.hcmute.shopfee.constant.VNPayConstant.*;
import static com.hcmute.shopfee.constant.VNPayConstant.VNP_SECURE_HASH_KEY;

public class Refund {
    private final VNPay vnPay;

    public Refund(VNPay vnPay) {
        this.vnPay = vnPay;
    }
    /**
     * refundType = 02 refund toan phan <p>
     * refundType = 03 refund 1 phan <p>
     * timeId: thoi gian bill duoc tao <p>
     * invoiceCode: ma 8 chu so
     */
    public  Map<String, Object> refund(HttpServletRequest req, String timeId, String amount, String invoiceCode, String refundType)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String vnp_RequestId = vnPay.getRandomNumber(8);
        String vnp_Version = VNP_VERSION;
        String vnp_Command = REFUND_COMMAND;
        String vnp_TmnCode = vnPay.getTmnCode();
        String vnp_TransactionType = refundType ;
        String vnp_TxnRef = invoiceCode;
        String vnp_Amount = String.valueOf(Integer.parseInt(amount)*100);
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
        String vnp_TransactionNo = "";
        String vnp_TransactionDate = timeId ;
        String vnp_CreateBy = "ADMIN";

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(VNP_TIME_ZONE));
        SimpleDateFormat formatter = new SimpleDateFormat(VNP_TIME_FORMAT);

        String vnp_CreateDate = formatter.format(cld.getTime());
        String vnp_IpAddr = vnPay.getIpAddress(req);
        JsonObject vnp_Params = new JsonObject ();

        vnp_Params.addProperty(VNP_REQ_ID_KEY, vnp_RequestId);
        vnp_Params.addProperty(VNP_VERSION_KEY, vnp_Version);
        vnp_Params.addProperty(VNP_COMMAND_KEY, vnp_Command);
        vnp_Params.addProperty(VNP_TMN_CODE_KEY, vnp_TmnCode);
        vnp_Params.addProperty(VNP_TRANSACTION_TYPE_KEY, vnp_TransactionType);
        vnp_Params.addProperty(VNP_TXN_REF_KEY, vnp_TxnRef);
        vnp_Params.addProperty(VNP_AMOUNT_KEY, vnp_Amount);
        vnp_Params.addProperty(VNP_ORDER_INFO_KEY, vnp_OrderInfo);

        if(vnp_TransactionNo != null && !vnp_TransactionNo.isEmpty())
        {
            vnp_Params.addProperty(VNP_TRANSACTION_NO_KEY, "{get value of vnp_TransactionNo}");
        }

        vnp_Params.addProperty(VNP_TRANSACTION_DATE_KEY, vnp_TransactionDate);
        vnp_Params.addProperty(VNP_CREATE_BY_KEY, vnp_CreateBy);
        vnp_Params.addProperty(VNP_CREATE_DATE_KEY, vnp_CreateDate);
        vnp_Params.addProperty(VNP_IP_ADDRESS_KEY, vnp_IpAddr);

        String hash_Data = vnp_RequestId + "|" + vnp_Version + "|" + vnp_Command + "|" + vnp_TmnCode + "|" +
                vnp_TransactionType + "|" + vnp_TxnRef + "|" + vnp_Amount + "|" + vnp_TransactionNo + "|"
                + vnp_TransactionDate + "|" + vnp_CreateBy + "|" + vnp_CreateDate + "|" + vnp_IpAddr + "|" + vnp_OrderInfo;

        String vnp_SecureHash = vnPay.hmacSHA512(vnPay.getSecretKey(), hash_Data.toString());

        vnp_Params.addProperty(VNP_SECURE_HASH_KEY, vnp_SecureHash);

        URL url = new URL (VNPay.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("nSending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + vnp_Params);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        System.out.println(response.toString());
        return objectMapper.readValue(response.toString(), Map.class);
    }
}
