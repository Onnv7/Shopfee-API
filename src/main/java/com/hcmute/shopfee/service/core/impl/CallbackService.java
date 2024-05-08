package com.hcmute.shopfee.service.core.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.shopfee.dto.common.vnpay.VnpayCallbackResponse;
import com.hcmute.shopfee.dto.common.zalopay.CallBackDto;
import com.hcmute.shopfee.dto.common.zalopay.CallbackDataRequest;
import com.hcmute.shopfee.dto.common.zalopay.ZaloCallbackResponse;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import com.hcmute.shopfee.entity.sql.database.payment.VNPayEntity;
import com.hcmute.shopfee.entity.sql.database.payment.ZaloPayEntity;
import com.hcmute.shopfee.enums.TransactionStatus;
import com.hcmute.shopfee.module.vnpay.VNPay;
import com.hcmute.shopfee.module.vnpay.VNPayConstant;
import com.hcmute.shopfee.module.vnpay.VNPayUtils;
import com.hcmute.shopfee.module.zalopay.ZaloPay;
import com.hcmute.shopfee.module.zalopay.ZaloPayUtils;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import com.hcmute.shopfee.repository.database.payment.VNPayRepository;
import com.hcmute.shopfee.repository.database.payment.ZaloPayRepository;
import com.hcmute.shopfee.service.core.ICallbackService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CallbackService implements ICallbackService {
    private final VNPayRepository vnpayRepository;
    private final TransactionRepository transactionRepository;
    private final VNPay vnPay;
    private final ZaloPay zaloPay;
    private final ZaloPayRepository zaloPayRepository;
    public VnpayCallbackResponse processCallback(HttpServletRequest request) throws UnsupportedEncodingException {

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
                    if(transactionEntity.getStatus() == TransactionStatus.UNPAID) {
                        if("00".equals(request.getParameter(VNPayConstant.VNP_RESPONSE_CODE))) {
                            transactionEntity.setTotalPaid(orderBillEntity.getTotalPayment());
                            transactionEntity.setStatus(TransactionStatus.PAID);
                        }
                        else {
                            transactionEntity.setTotalPaid(orderBillEntity.getTotalPayment());
                            transactionEntity.setStatus(TransactionStatus.PAID);
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

    public ZaloCallbackResponse processCallback(CallBackDto body) throws JsonProcessingException {
        ZaloCallbackResponse response = new ZaloCallbackResponse();
        String reqMac = ZaloPayUtils.hmacSha256(zaloPay.getKey2(), body.getData());
        if(reqMac.equals(body.getMac())) {
            ObjectMapper mapper = new ObjectMapper();
            CallbackDataRequest dataRequest = mapper.readValue(body.getData(), CallbackDataRequest.class);

            ZaloPayEntity zaloPay = zaloPayRepository.findByAppTransactionId(dataRequest.getAppTransId())
                    .orElse(null);

            if(zaloPay != null) {
                TransactionEntity transaction = zaloPay.getTransaction();
                if(transaction.getOrderBill().getTotalPayment() != dataRequest.getAmount()) {
                    response.setReturnCode(-1);
                    response.setReturnMessage("exception");
                } else {
                    transaction.setTotalPaid((long) dataRequest.getAmount());
                    transaction.setStatus(TransactionStatus.PAID);
                    zaloPay.setZalopayTransactionId(String.valueOf(dataRequest.getZpTransId()));

                    response.setReturnCode(1);
                    response.setReturnMessage("success");

                    transactionRepository.save(transaction);
                }
            } else {
                response.setReturnCode(-1);
                response.setReturnMessage("exception");
            }
        }
        else {
            response.setReturnCode(0);
            response.setReturnMessage("invalid callback");
        }
        return response;

    }


}
