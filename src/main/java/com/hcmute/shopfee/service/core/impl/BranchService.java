package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.CreateBranchRequest;
import com.hcmute.shopfee.dto.request.UpdateBranchRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.entity.database.BranchEntity;
import com.hcmute.shopfee.enums.BranchStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.module.goong.distancematrix.reponse.DistanceMatrixResponse;
import com.hcmute.shopfee.repository.database.BranchRepository;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.common.GoongService;
import com.hcmute.shopfee.service.core.IBranchService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.DateUtils;
import com.hcmute.shopfee.utils.LocationUtils;
import com.hcmute.shopfee.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Time;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;

import static com.hcmute.shopfee.constant.ShopfeeConstant.OPERATING_RANGE_DISTANCE;

@Service
@RequiredArgsConstructor
public class BranchService implements IBranchService {
    private final BranchRepository branchRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;
    private final GoongService goongService;

    public BranchEntity getNearestBranchAndValidateTime(Double lat, Double lng, Time timeToCheck) {
        // TODO: xem có status thì check status cửa hàng
        List<BranchEntity> branchEntityList = branchRepository.findByStatus(BranchStatus.ACTIVE);
        List<String> destinationCoordinatesList = LocationUtils.getCoordinatesListFromBranchList(branchEntityList);
        String clientCoordinates = lat + "," + lng;
        List<DistanceMatrixResponse.Row.Element.Distance> distanceList = goongService.getDistanceFromClientToBranches(clientCoordinates, destinationCoordinatesList, "bike");
        int branchListSize = branchEntityList.size();
        if(branchListSize == 0) {
            throw new CustomException(ErrorConstant.NOT_FOUND, "There are no active branches");
        }
        BranchEntity nearestBranch = branchEntityList.get(0);
        int minDistance = distanceList.get(0).getValue();

        for (int i = 0; i < branchListSize; i++) {
            if (distanceList.get(i).getValue() > OPERATING_RANGE_DISTANCE) {
                continue;
            }

            if (timeToCheck.after(branchEntityList.get(i).getCloseTime()) || timeToCheck.before(branchEntityList.get(i).getOpenTime())) {
                continue;
            }
            if (distanceList.get(i).getValue() < minDistance) {
                nearestBranch = branchEntityList.get(i);
                minDistance = distanceList.get(i).getValue();
            }
        }

        if (minDistance > OPERATING_RANGE_DISTANCE || timeToCheck.after(nearestBranch.getCloseTime()) || timeToCheck.before(nearestBranch.getOpenTime())) {
            throw new CustomException(ErrorConstant.NOT_FOUND, "Can't find a branch that can serve your current location and time");
        }

        return nearestBranch;

    }

    @Override
    public void createBranch(CreateBranchRequest body) {
        BranchEntity branch = modelMapperService.mapClass(body, BranchEntity.class);
        branch.setStatus(BranchStatus.INACTIVE);
        byte[] originalImage = new byte[0];
        try {
            originalImage = body.getImage().getBytes();

            HashMap<String, String> imageUploaded = cloudinaryService.uploadFileToFolder(
                    CloudinaryConstant.PRODUCT_PATH,
                    StringUtils.generateFileName(body.getName(), "branch"),
                    originalImage
            );
            branch.setImageId(imageUploaded.get(CloudinaryConstant.PUBLIC_ID));
            branch.setImageUrl(imageUploaded.get(CloudinaryConstant.URL_PROPERTY));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        branchRepository.save(branch);
    }

    @Override
    public void updateBranchById(UpdateBranchRequest body, String id) {
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.BRANCH_ID_NOT_FOUND + id));
        if (body.getImage() != null) {
            byte[] originalImage = new byte[0];
            try {
                originalImage = body.getImage().getBytes();

                HashMap<String, String> imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.PRODUCT_PATH,
                        StringUtils.generateFileName(body.getName(), "branch"),
                        originalImage
                );
                branch.setImageId(imageUploaded.get(CloudinaryConstant.PUBLIC_ID));
                branch.setImageUrl(imageUploaded.get(CloudinaryConstant.URL_PROPERTY));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        modelMapperService.map(body, branch);
        branchRepository.save(branch);
    }

    // TODO: branch hiện đăng xóa cứng
    @Override
    public void deleteBranchById(String id) {
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.BRANCH_ID_NOT_FOUND + id));
        branchRepository.deleteById(id);
        try {
            cloudinaryService.deleteImage(branch.getImageId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GetAllBranchResponse getBranchList(int page, int size) {
        GetAllBranchResponse data = new GetAllBranchResponse();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<BranchEntity> branchPage = branchRepository.findAll(pageable);
        data.setTotalPage(branchPage.getTotalPages());
        data.setBranchList(GetAllBranchResponse.BranchInfo.fromBranchEntityList(branchPage.getContent()));
        return data;
    }

    @Override
    public GetBranchNearestResponse getBranchNearest(Double latitude, Double longitude, Time time) {
        BranchEntity branchEntity = getNearestBranchAndValidateTime(latitude, longitude, time);

        return GetBranchNearestResponse.fromBranchEntity(branchEntity);
    }

    @Override
    public GetBranchDetailByIdResponse getBranchDetailById(String branchId) {
        BranchEntity branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.BRANCH_ID_NOT_FOUND + branchId));
        GetBranchDetailByIdResponse data = modelMapperService.mapClass(branch, GetBranchDetailByIdResponse.class);
        data.setOpenTime(DateUtils.getFormatTime(branch.getOpenTime()));
        data.setCloseTime(DateUtils.getFormatTime(branch.getCloseTime()));
        return data;
    }

    @Override
    public GetBranchViewByIdResponse getBranchViewById(String branchId) {
        BranchEntity branch = branchRepository.findByIdAndStatus(branchId, BranchStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.BRANCH_ID_NOT_FOUND + branchId));
        return GetBranchViewByIdResponse.fromBranchEntity(branch);
    }

    @Override
    public GetBranchViewListResponse getBranchViewList(boolean isGetAll, Double latitude, Double longitude, String key, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<BranchEntity> branchPage = null;

        if (key != null) {
            branchPage = branchRepository.getBranchByStatusAndKey(BranchStatus.ACTIVE.name(), key, pageable);
        } else {
            branchPage = branchRepository.findByStatus(BranchStatus.ACTIVE, pageable);
        }

        List<BranchEntity> branchEntityList = branchPage.getContent();
        List<String> destinationCoordinatesList = LocationUtils.getCoordinatesListFromBranchList(branchEntityList);

        String clientCoordinates = latitude + "," + longitude;

        List<DistanceMatrixResponse.Row.Element.Distance> distanceList = goongService.getDistanceFromClientToBranches(clientCoordinates, destinationCoordinatesList, "bike");
        GetBranchViewListResponse data = new GetBranchViewListResponse();
        data.setTotalPage(branchPage.getTotalPages());
        data.setBranchList(GetBranchViewListResponse.fromBranchEntityListAndFilterDistance(isGetAll, branchPage.getContent(), distanceList));
        return data;
    }

}
