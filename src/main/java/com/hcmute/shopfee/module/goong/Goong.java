package com.hcmute.shopfee.module.goong;

import com.hcmute.shopfee.module.goong.distancematrix.GoongDistanceMatrix;

public class Goong {
    private String API_KEY;

    public String getAPI_KEY() {
        return API_KEY;
    }

    public Goong(String API_KEY) {
        this.API_KEY = API_KEY;
    }
    public GoongDistanceMatrix getDistanceMatrix() {
        return new GoongDistanceMatrix(this);
    }
}
