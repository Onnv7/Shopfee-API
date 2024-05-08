package com.hcmute.shopfee.service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.shopfee.module.zalopay.ZaloPay;
import com.hcmute.shopfee.dto.common.zalopay.CreateOrderZaloPayResponse;
import com.hcmute.shopfee.dto.common.zalopay.GetOrderZaloPayResponse;
import com.hcmute.shopfee.dto.common.zalopay.RefundRequestDTO;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import com.hcmute.shopfee.repository.database.payment.ZaloPayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ZaloPayService {
    private final ZaloPay zaloPay;
    private final ZaloPayRepository zaloPayRepository;
    private final TransactionRepository transactionRepository;


    public CreateOrderZaloPayResponse createOrderTransaction(Long amount)  {
        try {
            Map<String, Object> orderResponse = zaloPay.createOrderZaloPay("shopfee", amount);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(orderResponse, CreateOrderZaloPayResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GetOrderZaloPayResponse getOrderTransactionInformation(String appTransId) {
        try {
            Map<String, Object> orderResponse = zaloPay.getOrder(appTransId);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(orderResponse, GetOrderZaloPayResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> sendRefund(RefundRequestDTO request) throws IOException, URISyntaxException {
        return zaloPay.sendRefund(request.getZpTransId(), request.getAmount(), request.getDescription());
    }

    public Map<String, Object> getStatusRefund(String refundId) throws IOException, URISyntaxException {
        return zaloPay.getStatusRefund(refundId);
    }
}
