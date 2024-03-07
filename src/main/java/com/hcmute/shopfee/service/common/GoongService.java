package com.hcmute.shopfee.service.common;

import com.hcmute.shopfee.module.goong.Goong;
import com.hcmute.shopfee.module.goong.distancematrix.reponse.DistanceMatrixResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoongService {
    private final Goong goong;

    public List<DistanceMatrixResponse.Row.Element.Distance> getDistanceFromClientToBranches(String clientLocation, List<String> branchLocationList, String vehicle) {
        StringBuilder branchLocations = new StringBuilder();
        for (String branch : branchLocationList) {
            branchLocations.append(branch).append("|");
        }
        branchLocations = new StringBuilder(branchLocations.substring(0, branchLocations.length() - 1));
        DistanceMatrixResponse result = goong.getDistanceMatrix().getDistanceMatrix(clientLocation, branchLocations.toString(), vehicle);
        List<DistanceMatrixResponse.Row.Element.Distance> resultList = new ArrayList<>();
        Arrays.stream(result.getRows()[0].getElements()).forEach(e -> {
            resultList.add(e.getDistance());
        });
        return resultList;
    }
}
