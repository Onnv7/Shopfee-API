package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.CloudinaryUploadResponse;
import com.hcmute.shopfee.dto.request.CreateBannerRequest;
import com.hcmute.shopfee.dto.request.UpdateBannerRequest;
import com.hcmute.shopfee.dto.response.GetBannerDetailResponse;
import com.hcmute.shopfee.dto.response.GetBannerListResponse;
import com.hcmute.shopfee.dto.response.GetVisibleBannerListResponse;
import com.hcmute.shopfee.entity.sql.database.BannerEntity;
import com.hcmute.shopfee.enums.BannerStatus;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.BannerRepository;
import com.hcmute.shopfee.service.core.IBannerService;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
            CloudinaryUploadResponse bannerImage = cloudinaryService.uploadFileToFolder(
                    CloudinaryConstant.BANNER_PATH,
                    StringUtils.generateFileName(body.getName(), "banner"),
                    body.getImage().getBytes()
            );
            banner.setImageUrl(bannerImage.getUrl());
            banner.setCloudinaryImageId(bannerImage.getPublicId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        banner.setDeleted(false);
        banner.setStatus(BannerStatus.VISIBLE);
        bannerRepository.save(banner);
    }

    @Override
    public void updateBannerById(UpdateBannerRequest body, String bannerId) {
        BannerEntity bannerEntity = bannerRepository.findByIdAndIsDeletedFalse(bannerId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BANNER_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + bannerId));
        modelMapperService.map(body, bannerEntity);
        if(body.getImage() != null) {
            try {
                // TODO: kiểm tra lại tính transaction khi xóa thành công -> upload new không thành công
                cloudinaryService.deleteImage(bannerEntity.getCloudinaryImageId());

                CloudinaryUploadResponse bannerImage = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.BANNER_PATH,
                        StringUtils.generateFileName(body.getName(), "banner"),
                        body.getImage().getBytes()
                );

                bannerEntity.setImageUrl(bannerImage.getUrl());
                bannerEntity.setCloudinaryImageId(bannerImage.getPublicId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        bannerRepository.save(bannerEntity);
    }

    @Override
    public void deleteBannerById(String bannerId) {
        BannerEntity bannerCollection = bannerRepository.findByIdAndIsDeletedFalse(bannerId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BANNER_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + bannerId));
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
        return modelMapperService.mapList(bannerRepository.findByStatusAndIsDeletedFalse(BannerStatus.VISIBLE), GetVisibleBannerListResponse.class);
    }

    @Override
    public GetBannerDetailResponse getBannerDetailsById(String bannerId) {
        BannerEntity banner = bannerRepository.findByIdAndIsDeletedFalse(bannerId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BANNER_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + bannerId));

        return modelMapperService.mapClass(banner, GetBannerDetailResponse.class);
    }
}
