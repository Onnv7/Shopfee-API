package com.hcmute.shopfee.service.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hcmute.shopfee.module.ahamove.Ahamove;
import com.hcmute.shopfee.module.ahamove.masterdata.response.EstimateOrderFeeResponse;
import com.hcmute.shopfee.module.ahamove.order.DestinationPoint;
import com.hcmute.shopfee.module.ahamove.order.OrderItem;
import com.hcmute.shopfee.module.ahamove.order.PayMethod;
import com.hcmute.shopfee.module.ahamove.order.StartingPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AhamoveService {
    private final Ahamove ahamove;
    public int getShippingFee(Double latOrigin, Double lngOrigin, Double latDestination, Double lngDestination) {
        EstimateOrderFeeResponse shippingResponse = ahamove.getEstimateOrderFee(latOrigin, lngOrigin, latDestination, lngDestination);

        return shippingResponse.getDistanceFee();
    }

    public Map<String, Object> createOrder(StartingPoint startingPoint, DestinationPoint destinationPoint, List<OrderItem> items, PayMethod payMethod) throws JsonProcessingException {
        return ahamove.createOrder(startingPoint, destinationPoint, items, payMethod);
    }
}
