package com.hcmute.shopfee.service.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hcmute.shopfee.constant.VNPayConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import com.hcmute.shopfee.entity.sql.database.payment.VNPayEntity;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.module.vnpay.VNPay;
import com.hcmute.shopfee.module.vnpay.VNPayUtils;
import com.hcmute.shopfee.module.vnpay.transaction.dto.PreTransactionInfo;
import com.hcmute.shopfee.module.vnpay.querydr.response.TransactionInfoQuery;
import com.hcmute.shopfee.module.vnpay.transaction.dto.VnpayCallbackData;
import com.hcmute.shopfee.module.vnpay.transaction.dto.VnpayCallbackResponse;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import com.hcmute.shopfee.repository.database.payment.VNPayRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayService {
    private final VNPay vnPay;
    private final VNPayRepository vnpayRepository;
    private final OrderBillRepository orderBillRepository;
    private final TransactionRepository transactionRepository;

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
                ipAddress = VNPayUtils.getIpAddress(request);
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

    public VnpayCallbackResponse processCallback(HttpServletRequest request) throws JsonProcessingException, UnsupportedEncodingException {

        VnpayCallbackResponse response = new VnpayCallbackResponse();

        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey(VNPayConstant.VNP_SECURE_HASH_TYPE_KEY)) {
            fields.remove(VNPayConstant.VNP_SECURE_HASH_TYPE_KEY);
        }
        if (fields.containsKey(VNPayConstant.VNP_SECURE_HASH_KEY)) {
            fields.remove(VNPayConstant.VNP_SECURE_HASH_KEY);
        }

        String signValue = VNPayUtils.hashAllFields(fields, vnPay.getSecretKey());
        if (signValue.equals(vnp_SecureHash)) {
            VNPayEntity vnPayEntity = vnpayRepository.findByInvoiceCode(fields.get(VNPayConstant.VNP_TXN_REF_KEY).toString())
                    .orElse(null);

            if(vnPayEntity != null) {
                OrderBillEntity orderBillEntity = vnPayEntity.getTransaction().getOrderBill();
                TransactionEntity transactionEntity = orderBillEntity.getTransaction();
                if(orderBillEntity.getTotalPayment() == Long.parseLong((String) fields.get(VNPayConstant.VNP_AMOUNT_KEY))/ 100) {
                    if(transactionEntity.getStatus() == PaymentStatus.UNPAID) {
                        if("00".equals(request.getParameter(VNPayConstant.VNP_RESPONSE_CODE))) {
                            transactionEntity.setTotalPaid(orderBillEntity.getTotalPayment());
                            transactionEntity.setStatus(PaymentStatus.PAID);
                        }
                        else {
                            transactionEntity.setTotalPaid(orderBillEntity.getTotalPayment());
                            transactionEntity.setStatus(PaymentStatus.PAID);
                        }
                        transactionRepository.save(transactionEntity);
                        response.setRspCode("00");
                        response.setMessage("Confirm Success");
                    }
                    else {
                        response.setRspCode("02");
                        response.setMessage("Order already confirmed");
                    }
                }
                else {
                    response.setRspCode("04");
                    response.setMessage("Invalid Amount");
                }
            }
            else {
                response.setRspCode("01");
                response.setMessage("Order not found");
            }
        } else {
            response.setRspCode("97");
            response.setMessage("Invalid Checksum");
        }


        return response;
    }
}
