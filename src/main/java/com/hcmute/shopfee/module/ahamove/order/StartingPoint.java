package com.hcmute.shopfee.module.ahamove.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StartingPoint {
    private Double lat;
    private Double lng;
    private String address;
    @JsonProperty("short_address")
    private String shortAddress;
    private String name;
    private String mobile;
}
