package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.enums.BannerStatus;
import lombok.Data;

@Data
public class GetBannerListResponse {
    private String id;
    private String name;
    private BannerStatus status;
    private String imageUrl;
}
