package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateBranchRequest;
import com.hcmute.shopfee.dto.request.UpdateBranchRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.entity.database.BranchEntity;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.module.goong.Goong;
import com.hcmute.shopfee.service.core.IBranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = BRANCH_CONTROLLER_TITLE)
@RestController
@RequestMapping(BRANCH_BASE_PATH)
@RequiredArgsConstructor
public class BranchController {
    private final IBranchService branchService;
    private final Goong goong;
    @Operation(summary = BRANCH_CREATE_SUM)
    @PostMapping(path = POST_BRANCH_CREATE_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<?>> createBranch(@ModelAttribute @Valid CreateBranchRequest body) {
        branchService.createBranch(body);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = BRANCH_UPDATE_BY_ID_SUM)
    @PutMapping(path = PUT_BRANCH_UPDATE_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<?>> updateBranchInfoById(@ModelAttribute @Valid UpdateBranchRequest body, @PathVariable(BRANCH_ID) String branchId) {
        branchService.updateBranchById(body, branchId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = BRANCH_DELETE_BY_ID_SUM)
    @DeleteMapping(path = DELETE_BRANCH_UPDATE_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> deleteBranchInfoById(@PathVariable(BRANCH_ID) String branchId) {
        branchService.deleteBranchById(branchId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.DELETED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = BRANCH_GET_ALL_SUM)
    @GetMapping(path = GET_BRANCH_ALL_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetAllBranchResponse>> getAllBranch(
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size
    ) {
        GetAllBranchResponse resData = branchService.getBranchList(page, size);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = BRANCH_GET_BRANCH_NEAREST_SUM)
    @GetMapping(path = GET_BRANCH_NEAREST_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetBranchNearestResponse>> getBranchNearest(
            @Parameter(name = "lat", required = true, example = "10.8005397")
            @RequestParam("lat")  Double lat,
            @Parameter(name = "lng", required = true, example = "106.6393208")
            @RequestParam("lng")  Double lng,
            @Parameter(name = "time", required = true, example = "07:00:00", description = "Time for customer receives the order")
            @RequestParam("time") Time time
    ) {
        GetBranchNearestResponse resData = branchService.getBranchNearest(lat, lng, time);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = BRANCH_GET_DETAIL_BY_ID_SUM)
    @GetMapping(path = GET_BRANCH_DETAIL_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetBranchDetailByIdResponse>> getBranchById(@PathVariable(BRANCH_ID) String branchId) {
        GetBranchDetailByIdResponse resData = branchService.getBranchDetailById(branchId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = BRANCH_GET_VIEW_BY_ID_SUM)
    @GetMapping(path = GET_BRANCH_VIEW_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetBranchViewByIdResponse>> getBranchViewById(@PathVariable(BRANCH_ID) String branchId) {
        GetBranchViewByIdResponse resData = branchService.getBranchViewById(branchId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = BRANCH_GET_VIEW_LIST_BY_ID_SUM)
    @GetMapping(path = GET_BRANCH_VIEW_LIST_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetBranchViewListResponse>> getBranchViewList(
            @Parameter(name = "all", description = "Get all or filter", required = false, example = "false")
            @RequestParam(name = "all", required = false, defaultValue = "false") boolean all,
            @Parameter(name = "lat", required = true, example = "10.8005397")
            @RequestParam("lat")  Double lat,
            @Parameter(name = "lng", required = true, example = "106.6393208")
            @RequestParam("lng")  Double lng,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,
            @Parameter(name = "key", description = "Key is name, address", required = false, example = "vo van ngan")
            @RequestParam(name = "key", required = false) String key
    ) {
        GetBranchViewListResponse resData = branchService.getBranchViewList(all, lat, lng, key, page, size);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
