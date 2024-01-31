package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.enums.BannerStatus;
import lombok.Data;

@Data
public class GetBannerDetailResponse {
    private String id;
    private String name;
    private String imageUrl;
    private BannerStatus status;
}
