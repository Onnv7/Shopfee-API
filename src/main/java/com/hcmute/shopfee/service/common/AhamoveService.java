package com.hcmute.shopfee.service.common;

import com.hcmute.shopfee.module.ahamove.Ahamove;
import com.hcmute.shopfee.module.ahamove.masterdata.estimateorderfee.EstimateOrderFee;
import com.hcmute.shopfee.module.ahamove.masterdata.estimateorderfee.response.EstimateOrderFeeResponse;
import com.hcmute.shopfee.module.goong.distancematrix.reponse.DistanceMatrixResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AhamoveService {
    private final Ahamove ahamove;
    public int getShippingFee(Double latOrigin, Double lngOrigin, Double latDestination, Double lngDestination) {
        EstimateOrderFeeResponse shippingResponse = ahamove.getEstimateOrderFee(latOrigin, lngOrigin, latDestination, lngDestination);

        return shippingResponse.getDistanceFee();
    }
}
