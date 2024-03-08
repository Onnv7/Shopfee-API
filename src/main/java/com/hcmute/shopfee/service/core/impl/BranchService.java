package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.CreateBranchRequest;
import com.hcmute.shopfee.dto.request.UpdateBranchRequest;
import com.hcmute.shopfee.dto.response.GetAllBranchResponse;
import com.hcmute.shopfee.dto.response.GetBranchDetailByIdResponse;
import com.hcmute.shopfee.dto.response.GetBranchViewByIdResponse;
import com.hcmute.shopfee.dto.response.GetBranchViewListResponse;
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
import com.hcmute.shopfee.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService implements IBranchService {
    private final BranchRepository branchRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;
    private final GoongService goongService;
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
    public GetBranchDetailByIdResponse getBranchDetailById(String branchId) {
        BranchEntity branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.BRANCH_ID_NOT_FOUND + branchId));
        GetBranchDetailByIdResponse data = modelMapperService.mapClass(branch, GetBranchDetailByIdResponse.class);
        data.setOpenTime(DateUtils.getFormatTime(branch.getOpenTime()));
        data.setCloseTime(DateUtils.getFormatTime(branch.getCloseTime()));
        return data;
    }

    @Override
    public GetBranchViewByIdResponse getBranchViewById(Long branchId) {
        BranchEntity branch = branchRepository.findByIdAndStatus(branchId, BranchStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.BRANCH_ID_NOT_FOUND + branchId));
        return GetBranchViewByIdResponse.fromBranchEntity(branch);
    }

    @Override
    public GetBranchViewListResponse getBranchViewList(Double latitude, Double longitude, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<BranchEntity> branchPage = branchRepository.findByStatus(BranchStatus.ACTIVE, pageable);
        List<BranchEntity> branchEntityList = branchPage.getContent();
        List<String> destinationCoordinatesList = getCoordinatesListFromBranchList(branchEntityList);

        String clientCoordinates = latitude + "," + longitude;

        List<DistanceMatrixResponse.Row.Element.Distance> distanceList = goongService.getDistanceFromClientToBranches(clientCoordinates, destinationCoordinatesList, "bike");
        GetBranchViewListResponse data = new GetBranchViewListResponse();
        data.setTotalPage(branchPage.getTotalPages());
        data.setBranchList(GetBranchViewListResponse.fromBranchEntityListAndFilterDistance(branchPage.getContent(), distanceList));
        return data;
    }
    public List<String> getCoordinatesListFromBranchList(List<BranchEntity> branchEntityList) {
        List<String> destinationCoordinatesList = new ArrayList<>();
        for(BranchEntity branchEntity : branchEntityList) {
            String locationFormat = "%s,%s";
            String coordinates = String.format(locationFormat, branchEntity.getLatitude(), branchEntity.getLongitude());
            destinationCoordinatesList.add(coordinates);
        }
        return destinationCoordinatesList;
    }
}
