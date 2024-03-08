package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.CreateBannerRequest;
import com.hcmute.shopfee.dto.request.UpdateBannerRequest;
import com.hcmute.shopfee.dto.response.GetBannerDetailResponse;
import com.hcmute.shopfee.dto.response.GetBannerListResponse;
import com.hcmute.shopfee.dto.response.GetVisibleBannerListResponse;
import com.hcmute.shopfee.entity.database.BannerEntity;
import com.hcmute.shopfee.enums.BannerStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.BannerRepository;
import com.hcmute.shopfee.service.core.IBannerService;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService implements IBannerService {
    private final BannerRepository bannerRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;

    @Override
    public void createBanner(CreateBannerRequest body) {
        BannerEntity banner = modelMapperService.mapClass(body, BannerEntity.class);

        try {
            HashMap<String, String> bannerImage = cloudinaryService.uploadFileToFolder(
                    CloudinaryConstant.BANNER_PATH,
                    StringUtils.generateFileName(body.getName(), "banner"),
                    body.getImage().getBytes()
            );
            banner.setImageUrl(bannerImage.get(CloudinaryConstant.URL_PROPERTY));
            banner.setImageId(bannerImage.get(CloudinaryConstant.PUBLIC_ID));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        banner.setDeleted(false);
        banner.setStatus(BannerStatus.HIDDEN);
        bannerRepository.save(banner);
    }

    @Override
    public void updateBannerById(UpdateBannerRequest body, String bannerId) {
        BannerEntity bannerEntity = bannerRepository.findByIdAndIsDeletedFalse(bannerId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.BANNER_ID_NOT_FOUND + bannerId));
        modelMapperService.map(body, bannerEntity);
        if(body.getImage() != null) {
            try {
                // TODO: kiểm tra lại tính transaction khi xóa thành công -> upload new không thành công
                cloudinaryService.deleteImage(bannerEntity.getImageId());

                HashMap<String, String> bannerImage = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.BANNER_PATH,
                        StringUtils.generateFileName(body.getName(), "banner"),
                        body.getImage().getBytes()
                );

                bannerEntity.setImageUrl(bannerImage.get(CloudinaryConstant.URL_PROPERTY));
                bannerEntity.setImageId(bannerImage.get(CloudinaryConstant.PUBLIC_ID));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        bannerRepository.save(bannerEntity);
    }

    @Override
    public void deleteBannerById(String bannerId) {
        BannerEntity bannerCollection = bannerRepository.findByIdAndIsDeletedFalse(bannerId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.BANNER_ID_NOT_FOUND + bannerId));
        bannerCollection.setDeleted(true);
        bannerRepository.save(bannerCollection);
    }

    @Override
    public List<GetBannerListResponse> getBannerList() {
        List<BannerEntity> bannerCollectionList = bannerRepository.findByIsDeletedFalse();
        return modelMapperService.mapList(bannerCollectionList, GetBannerListResponse.class);
    }

    @Override
    public List<GetVisibleBannerListResponse> getVisibleBannerList() {
        return modelMapperService.mapList(bannerRepository.findByIsDeletedFalseAndStatus(BannerStatus.VISIBLE), GetVisibleBannerListResponse.class);
    }

    @Override
    public GetBannerDetailResponse getBannerDetailsById(String id) {
        BannerEntity banner = bannerRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.BANNER_ID_NOT_FOUND + id));

        return modelMapperService.mapClass(banner, GetBannerDetailResponse.class);
    }
}
