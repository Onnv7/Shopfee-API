package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.CreateBannerRequest;
import com.hcmute.shopfee.dto.request.UpdateBannerRequest;
import com.hcmute.shopfee.dto.response.GetBannerDetailResponse;
import com.hcmute.shopfee.dto.response.GetBannerListResponse;
import com.hcmute.shopfee.dto.response.GetVisibleBannerListResponse;

import java.util.List;

public interface IBannerService {
    void createBanner(CreateBannerRequest body);
    void updateBannerById(UpdateBannerRequest body, String bannerId);
    void deleteBannerById(String bannerId);
    List<GetBannerListResponse> getBannerList();
    List<GetVisibleBannerListResponse> getVisibleBannerList();
    GetBannerDetailResponse getBannerDetailsById(String id);
}
