package com.hcmute.shopfee.dto.common;

import lombok.Data;

import java.util.List;

@Data
public class RecommendationResponse{
    List<String> recommendations;
}