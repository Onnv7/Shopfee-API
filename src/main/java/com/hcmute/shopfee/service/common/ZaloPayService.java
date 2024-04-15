package com.hcmute.shopfee.service.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import com.hcmute.shopfee.entity.sql.database.payment.ZaloPayEntity;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.module.zalopay.ZaloPay;
import com.hcmute.shopfee.module.zalopay.order.dto.request.CallBackDto;
import com.hcmute.shopfee.module.zalopay.order.dto.request.CallbackDataRequest;
import com.hcmute.shopfee.module.zalopay.order.dto.request.CreateOrderZaloPayRequest;
import com.hcmute.shopfee.module.zalopay.order.dto.request.GetOrderZaloPayRequest;
import com.hcmute.shopfee.module.zalopay.order.dto.response.ZaloCallbackResponse;
import com.hcmute.shopfee.module.zalopay.order.dto.response.CreateOrderZaloPayResponse;
import com.hcmute.shopfee.module.zalopay.order.dto.response.GetOrderZaloPayResponse;
import com.hcmute.shopfee.module.zalopay.refund.dto.request.RefundRequestDTO;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import com.hcmute.shopfee.repository.database.payment.ZaloPayRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;
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

    public GetOrderZaloPayResponse getOrderTransactionInformation(String appTransId) {
        GetOrderZaloPayRequest body = new GetOrderZaloPayRequest();
        body.setAppTransId(appTransId);
        try {
            return zaloPay.getOrder(body);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public ZaloCallbackResponse processCallback(CallBackDto body) throws JsonProcessingException {
        ZaloCallbackResponse response = new ZaloCallbackResponse();
        String reqMac = Hex.encodeHexString(HmacUtils.hmacSha256(zaloPay.getKey2().getBytes(), body.getData().getBytes()));
        if(reqMac.equals(body.getMac())) {
            CallbackDataRequest dataRequest = zaloPay.getDataCallBack(body.getData());
            ZaloPayEntity zaloPay = zaloPayRepository.findByAppTransactionId(dataRequest.getAppTransId())
                    .orElse(null);

            if(zaloPay != null) {
                TransactionEntity transaction = zaloPay.getTransaction();
                if(transaction.getOrderBill().getTotalPayment() != dataRequest.getAmount()) {
                    response.setReturnCode(-1);
                    response.setReturnMessage("exception");
                } else {
                    transaction.setTotalPaid((long) dataRequest.getAmount());
                    transaction.setStatus(PaymentStatus.PAID);
                    zaloPay.setZalopayTransactionId(dataRequest.getZpTransId());

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
    public CreateOrderZaloPayResponse createOrderTest(CreateOrderZaloPayRequest createOrderZaloPayRequest) throws IOException {
        return zaloPay.createOrderZaloPay(createOrderZaloPayRequest);
    }

    public GetOrderZaloPayResponse getOrderTest(GetOrderZaloPayRequest body) throws IOException, URISyntaxException {
        return zaloPay.getOrder(body);
    }

    public Map<String, Object> sendRefund(RefundRequestDTO request) throws IOException, URISyntaxException {
        return zaloPay.sendRefund(request);
    }

    public Map<String, Object> getStatusRefund(String refundId) throws IOException, URISyntaxException {
        return zaloPay.getStatusRefund(refundId);
    }
}
