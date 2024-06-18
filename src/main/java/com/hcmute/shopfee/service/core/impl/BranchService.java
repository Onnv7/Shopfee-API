package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.BranchDistanceDto;
import com.hcmute.shopfee.dto.common.CloudinaryUploadResponse;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.payload.request.CreateBranchRequest;
import com.hcmute.shopfee.payload.request.UpdateBranchRequest;
import com.hcmute.shopfee.entity.sql.database.BranchEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.BranchProductId;
import com.hcmute.shopfee.entity.sql.database.product.BranchProductEntity;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.BranchProductStatus;
import com.hcmute.shopfee.enums.BranchStatus;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.module.goong.distancematrix.reponse.DistanceMatrixResponse;
import com.hcmute.shopfee.payload.response.*;
import com.hcmute.shopfee.repository.database.BranchRepository;
import com.hcmute.shopfee.repository.database.product.BranchProductRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.hcmute.shopfee.constant.ShopfeeConstant.OPERATING_RANGE_DISTANCE_METTER;

@Service
@RequiredArgsConstructor
public class BranchService implements IBranchService {
    private final BranchRepository branchRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;
    private final GoongService goongService;
    private final ProductRepository productRepository;
    private final BranchProductRepository branchProductRepository;

    public BranchEntity getNearestBranchAndValidateTime(Double lat, Double lng, Time timeToCheck, OrderType orderType) {
        List<BranchEntity> branchEntityList = branchRepository.findByStatus(BranchStatus.ACTIVE);
        List<String> destinationCoordinatesList = LocationUtils.getCoordinatesListFromBranchList(branchEntityList);
        String clientCoordinates = lat + "," + lng;
        List<DistanceMatrixResponse.Row.Element.Distance> distanceList = goongService.getDistanceFromClientToBranches(clientCoordinates, destinationCoordinatesList, "bike");
        int branchListSize = branchEntityList.size();
        if (branchListSize == 0) {
            throw new ShopfeeException(ShopfeeErrorCode.BRANCH_NOT_FOUND, "There are no active branches");
        }
        BranchEntity nearestBranch = branchEntityList.get(0);
        int minDistance = distanceList.get(0).getValue();

        for (int i = 0; i < branchListSize; i++) {
            if (orderType == OrderType.SHIPPING && distanceList.get(i).getValue() > OPERATING_RANGE_DISTANCE_METTER) {
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

        if (minDistance > OPERATING_RANGE_DISTANCE_METTER || timeToCheck.after(nearestBranch.getCloseTime()) || timeToCheck.before(nearestBranch.getOpenTime())) {
            throw new ShopfeeException(ShopfeeErrorCode.BRANCH_NOT_FOUND, "Can't find a branch that can serve your current location and time");
        }

        return nearestBranch;

    }

    public BranchEntity getNearestBranchForShippingOrder(Double lat, Double lng, List<OrderItemDto> itemList) {
        Time currentTime = DateUtils.getCurrentTime();

        List<BranchEntity> branchEntityList = branchRepository.findByStatus(BranchStatus.ACTIVE);

        List<String> destinationCoordinatesList = LocationUtils.getCoordinatesListFromBranchList(branchEntityList);
        String clientCoordinates = lat + "," + lng;

        List<DistanceMatrixResponse.Row.Element.Distance> distanceList = goongService.getDistanceFromClientToBranches(clientCoordinates, destinationCoordinatesList, "bike");
        int branchListSize = branchEntityList.size();

        List<BranchDistanceDto> branchDistanceList = new ArrayList<>();
        for (int i = 0; i < branchListSize; i++) {
            if (distanceList.get(i).getValue() > OPERATING_RANGE_DISTANCE_METTER) {
                branchEntityList.remove(i);
                distanceList.remove(i);
                branchListSize--;
                i--;
                continue;
            }

            if (currentTime.after(branchEntityList.get(i).getCloseTime()) || currentTime.before(branchEntityList.get(i).getOpenTime())) {
                branchEntityList.remove(i);
                distanceList.remove(i);
                branchListSize--;
                i--;
                continue;
            }
            branchDistanceList.add(new BranchDistanceDto(branchEntityList.get(i), distanceList.get(i).getValue()));

        }

        if (branchDistanceList.isEmpty()) {
            throw new ShopfeeException(ShopfeeErrorCode.BRANCH_NOT_FOUND, "Can't find a branch that can serve your current location and time");
        }

        // sap xep lai theo distance tu nho -> lon
        branchDistanceList.sort(Comparator.comparingInt(BranchDistanceDto::getDistance));


        // kiem tra item cua tung branch co distance tu nho -> lon
        BranchEntity branchValid = null;
        for (BranchDistanceDto branchDistanceDto : branchDistanceList) {
            boolean haveFullFillItem = true;
            for(OrderItemDto orderItemDto : itemList) {
                BranchProductEntity branchProductEntity = branchProductRepository.getProductBranchAvailable(orderItemDto.getProductId(), branchDistanceDto.getBranch().getId())
                        .orElse(null);
                if(branchProductEntity == null) {
                    haveFullFillItem = false;
                    break;
                }
            }
            if(haveFullFillItem) {
                branchValid = branchDistanceDto.getBranch();
            }
        }


        return branchValid;

    }

    @Transactional
    @Override
    public void createBranch(CreateBranchRequest body) throws ExecutionException, InterruptedException {
        BranchEntity branch = modelMapperService.mapClass(body, BranchEntity.class);
        branch.setStatus(BranchStatus.INACTIVE);
        byte[] originalImage = new byte[0];
        try {
            originalImage = body.getImage().getBytes();

            CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                    CloudinaryConstant.PRODUCT_PATH,
                    StringUtils.generateFileNameByTime(body.getName(), "branch"),
                    originalImage
            );
            branch.setCloudinaryImageId(imageUploaded.getPublicId());
            branch.setImageUrl(imageUploaded.getUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        branch = branchRepository.save(branch);
        List<ProductEntity> productEntityList = productRepository.findAll();
        for (ProductEntity product : productEntityList) {
            BranchProductId branchProductId = new BranchProductId(branch.getId(), product.getId());
            BranchProductEntity branchProductEntity = new BranchProductEntity();
            branchProductEntity.setId(branchProductId);
            branchProductEntity.setProduct(product);
            branchProductEntity.setBranch(branch);
            branchProductEntity.setStatus(BranchProductStatus.AVAILABLE);
            branchProductRepository.save(branchProductEntity);
        }
    }

    @Override
    public void updateBranchById(UpdateBranchRequest body, String id) {
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BRANCH_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + id));
        if (body.getImage() != null) {
            byte[] originalImage = new byte[0];
            try {
                originalImage = body.getImage().getBytes();

                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.PRODUCT_PATH,
                        StringUtils.generateFileNameByTime(body.getName(), "branch"),
                        originalImage
                );
                branch.setCloudinaryImageId(imageUploaded.getPublicId());
                branch.setImageUrl(imageUploaded.getUrl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        modelMapperService.map(body, branch);
        branchRepository.save(branch);
    }

    @Override
    public void deleteBranchById(String branchId) {
        BranchEntity branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BRANCH_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + branchId));
        if (branchRepository.countEmployeeByBranch(branchId) > 0 || branchRepository.countOrderBillByBranch(branchId) > 0) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.CANT_DELETE);
        }

        branchRepository.deleteById(branchId);
        try {
            cloudinaryService.deleteImage(branch.getCloudinaryImageId());
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
        BranchEntity branchEntity = getNearestBranchAndValidateTime(latitude, longitude, time, OrderType.ONSITE);

        return GetBranchNearestResponse.fromBranchEntity(branchEntity);
    }

    @Override
    public GetBranchDetailByIdResponse getBranchDetailById(String branchId) {
        BranchEntity branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BRANCH_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + branchId));
        GetBranchDetailByIdResponse data = modelMapperService.mapClass(branch, GetBranchDetailByIdResponse.class);
        data.setOpenTime(DateUtils.getFormatTime(branch.getOpenTime()));
        data.setCloseTime(DateUtils.getFormatTime(branch.getCloseTime()));
        return data;
    }

    @Override
    public GetBranchViewByIdResponse getBranchViewById(String branchId) {
        BranchEntity branch = branchRepository.findByIdAndStatus(branchId, BranchStatus.ACTIVE)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BRANCH_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + branchId));
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
        GetBranchViewListResponse data = new GetBranchViewListResponse();
        data.setTotalPage(branchPage.getTotalPages());

        List<BranchEntity> branchEntityList = branchPage.getContent();
        List<String> destinationCoordinatesList = LocationUtils.getCoordinatesListFromBranchList(branchEntityList);

        if (branchEntityList.isEmpty()) {
            data.setBranchList(new ArrayList<>());
            return data;
        }

        String clientCoordinates = latitude + "," + longitude;

        List<DistanceMatrixResponse.Row.Element.Distance> distanceList = goongService.getDistanceFromClientToBranches(clientCoordinates, destinationCoordinatesList, "bike");

        data.setBranchList(GetBranchViewListResponse.fromBranchEntityListAndFilterDistance(isGetAll, branchPage.getContent(), distanceList));
        return data;
    }

}
