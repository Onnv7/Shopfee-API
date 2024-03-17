package com.hcmute.shopfee.module.goong;

import com.hcmute.shopfee.module.goong.distancematrix.GoongDistanceMatrix;
import com.hcmute.shopfee.module.goong.distancematrix.reponse.DistanceMatrixResponse;

public class Goong {
    private final String API_KEY;
    private final GoongDistanceMatrix goongDistanceMatrix;

    public String getApiKey() {
        return API_KEY;
    }

    public Goong(String API_KEY) {
        this.API_KEY = API_KEY;
        this.goongDistanceMatrix = new GoongDistanceMatrix(this);
    }

    public DistanceMatrixResponse getDistanceMatrix(String origins, String destinations, String vehicle) {
        return goongDistanceMatrix.getDistanceMatrix(origins, destinations, vehicle);
    }
}
