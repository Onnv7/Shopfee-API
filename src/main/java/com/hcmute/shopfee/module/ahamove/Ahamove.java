package com.hcmute.shopfee.module.ahamove;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hcmute.shopfee.module.ahamove.masterdata.EstimateOrderFee;
import com.hcmute.shopfee.module.ahamove.masterdata.response.EstimateOrderFeeResponse;
import com.hcmute.shopfee.module.ahamove.order.*;

import java.util.List;
import java.util.Map;

public class Ahamove {
    private final String AHAMOVE_TOKEN;
    private final EstimateOrderFee estimateOrderFee;
    private final AhamoveOrder ahamoveOrder;

    public String getAhamoveToken() {
        return AHAMOVE_TOKEN;
    }

    public Ahamove(String token) {
        AHAMOVE_TOKEN = token;
        estimateOrderFee = new EstimateOrderFee(this);
        ahamoveOrder = new AhamoveOrder(this);
    }

    public EstimateOrderFeeResponse getEstimateOrderFee(Double latOrigin, Double lngOrigin, Double latDestination, Double lngDestination) {
        return estimateOrderFee.getEstimateOrderFee(latOrigin, lngOrigin, latDestination, lngDestination);
    }
    public Map<String, Object> createOrder(StartingPoint startingPoint, DestinationPoint destinationPoint, List<OrderItem> items, PayMethod payMethod) throws JsonProcessingException {
        return ahamoveOrder.createOrder(startingPoint, destinationPoint, items, payMethod);
    }
}
