package com.hcmute.shopfee.service.common;

import com.hcmute.shopfee.module.zalopay.ZaloPay;
import com.hcmute.shopfee.module.zalopay.order.dto.request.CreateOrderZaloPayRequest;
import com.hcmute.shopfee.module.zalopay.order.dto.request.GetOrderZaloPayRequest;
import com.hcmute.shopfee.module.zalopay.order.dto.response.CreateOrderZaloPayResponse;
import com.hcmute.shopfee.module.zalopay.order.dto.response.GetOrderZaloPayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class ZaloPayService {
    private final ZaloPay zaloPay;

    public CreateOrderZaloPayResponse createOrderTransaction(Long amount, String orderId)  {
        CreateOrderZaloPayRequest request = new CreateOrderZaloPayRequest();
        request.setAmount(amount);
        request.setOrderId(orderId);
        request.setAppUser("shopfee");
        try {
            return zaloPay.createOrderZaloPay(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GetOrderZaloPayResponse getOrderTransactionInformation(String zaloTransId) {
        GetOrderZaloPayRequest body = new GetOrderZaloPayRequest();
        body.setAppTransId(zaloTransId);
        try {
            return zaloPay.getOrder(body);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public CreateOrderZaloPayResponse createOrderTest(CreateOrderZaloPayRequest createOrderZaloPayRequest) throws IOException {
        return zaloPay.createOrderZaloPay(createOrderZaloPayRequest);
    }

    public GetOrderZaloPayResponse getOrderTest(GetOrderZaloPayRequest body) throws IOException, URISyntaxException {
        return zaloPay.getOrder(body);
    }
}
