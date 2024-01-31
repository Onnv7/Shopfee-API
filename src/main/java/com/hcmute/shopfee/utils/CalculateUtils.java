package com.hcmute.shopfee.utils;

public class CalculateUtils {
    private static final double EARTH_RADIUS = 6371;
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Chuyển đổi độ sang radian
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Tính chênh lệch giữa các kinh độ và vĩ độ
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // công thức haversine
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS * c;

        return distance;
    }
}
