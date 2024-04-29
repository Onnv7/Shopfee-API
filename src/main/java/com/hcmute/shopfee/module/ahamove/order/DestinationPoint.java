package com.hcmute.shopfee.module.ahamove.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DestinationPoint {
    private Double lat;
    private Double lng;
    private String address;
    private String name;
    private String mobile;
    private String remarks;
    private Long cod;
    @JsonProperty("tracking_number")
    private String trackingNumber;
}
