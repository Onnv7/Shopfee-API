package com.hcmute.shopfee.utils;

import com.hcmute.shopfee.entity.sql.database.BranchEntity;

import java.util.ArrayList;
import java.util.List;

public class LocationUtils {
    public static List<String> getCoordinatesListFromBranchList(List<BranchEntity> branchEntityList) {
        List<String> destinationCoordinatesList = new ArrayList<>();
        for(BranchEntity branchEntity : branchEntityList) {
            String locationFormat = "%s,%s";
            String coordinates = String.format(locationFormat, branchEntity.getLatitude(), branchEntity.getLongitude());
            destinationCoordinatesList.add(coordinates);
        }
        return destinationCoordinatesList;
    }
}
