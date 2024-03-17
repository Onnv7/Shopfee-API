package com.hcmute.shopfee.module.ahamove;

import com.hcmute.shopfee.module.ahamove.masterdata.estimateorderfee.EstimateOrderFee;
import com.hcmute.shopfee.module.ahamove.masterdata.estimateorderfee.response.EstimateOrderFeeResponse;

public class Ahamove {
    private final String AHAMOVE_TOKEN;
    private final EstimateOrderFee estimateOrderFee;

    public String getAhamoveToken() {
        return AHAMOVE_TOKEN;
    }

    public Ahamove(String token) {
        AHAMOVE_TOKEN = token;
        estimateOrderFee = new EstimateOrderFee(this);
    }

    public EstimateOrderFeeResponse getEstimateOrderFee(Double latOrigin, Double lngOrigin, Double latDestination, Double lngDestination) {
        return estimateOrderFee.getEstimateOrderFee(latOrigin, lngOrigin, latDestination, lngDestination);
    }
}
