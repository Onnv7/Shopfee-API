package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateBannerRequest;
import com.hcmute.shopfee.dto.request.UpdateBannerRequest;
import com.hcmute.shopfee.dto.response.GetBannerDetailResponse;
import com.hcmute.shopfee.dto.response.GetBannerListResponse;
import com.hcmute.shopfee.dto.response.GetVisibleBannerListResponse;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.IBannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = BANNER_CONTROLLER_TITLE)
@RestController
@RequiredArgsConstructor
@RequestMapping(BANNER_BASE_PATH)
public class BannerController {
    private final IBannerService bannerService;

    @Operation(summary = BANNER_CREATE_SUM)
    @PostMapping(path = POST_BANNER_CREATE_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity createBanner(@ModelAttribute @Valid CreateBannerRequest body) {
        bannerService.createBanner(body);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .success(true)
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }
    @Operation(summary = BANNER_UPDATE_BY_ID_SUM)
    @PutMapping(path = PUT_BANNER_UPDATE_BY_ID_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity updateBannerById(@ModelAttribute @Valid UpdateBannerRequest body, @PathVariable(BANNER_ID) String bannerId) {
        bannerService.updateBannerById(body, bannerId);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .success(true)
                .message(SuccessConstant.UPDATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
    @Operation(summary = BANNER_GET_LIST_SUM)
    @GetMapping(path = GET_BANNER_LIST_SUB_PATH)
    public ResponseEntity getBannerList() {
        List<GetBannerListResponse>  resData = bannerService.getBannerList();

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .success(true)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
    @Operation(summary = BANNER_GET_VISIBLE_LIST_SUM)
    @GetMapping(path = GET_BANNER_VISIBLE_LIST_SUB_PATH)
    public ResponseEntity getVisibleBannerList() {
        List<GetVisibleBannerListResponse> resData = bannerService.getVisibleBannerList();

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .success(true)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
    @Operation(summary = BANNER_GET_DETAILS_BY_ID_LIST_SUM)
    @GetMapping(path = GET_BANNER_DETAILS_BY_ID_SUB_PATH)
    public ResponseEntity getBannerDetailsById(@PathVariable(BANNER_ID) String bannerId) {
        GetBannerDetailResponse resData = bannerService.getBannerDetailsById(bannerId);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .success(true)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
    @Operation(summary = BANNER_DELETE_BY_ID_SUM)
    @DeleteMapping(path = DELETE_BANNER_BY_ID_SUB_PATH)
    public ResponseEntity deleteBannerById(@PathVariable(BANNER_ID) String bannerId) {
        bannerService.deleteBannerById(bannerId);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .success(true)
                .message(SuccessConstant.DELETED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
