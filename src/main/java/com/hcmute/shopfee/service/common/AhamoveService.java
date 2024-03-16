package com.hcmute.shopfee.service.common;

import com.hcmute.shopfee.module.ahamove.masterdata.estimateorderfee.EstimateOrderFee;
import com.hcmute.shopfee.module.ahamove.masterdata.estimateorderfee.response.EstimateOrderFeeResponse;
import com.hcmute.shopfee.module.goong.distancematrix.reponse.DistanceMatrixResponse;
import org.springframework.stereotype.Service;

@Service
public class AhamoveService {
    public int getShippingFee(Double latOrigin, Double lngOrigin, Double latDestination, Double lngDestination) {
        EstimateOrderFeeResponse shippingResponse = EstimateOrderFee.getEstimateOrderFee(latOrigin, lngOrigin, latDestination, lngDestination);

        return shippingResponse.getDistanceFee();
    }
}
