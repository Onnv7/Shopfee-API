package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateBranchRequest;
import com.hcmute.shopfee.dto.request.UpdateBranchRequest;
import com.hcmute.shopfee.entity.BranchEntity;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.IBranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = BRANCH_CREATE_SUM)
    @PostMapping(path = POST_BRANCH_CREATE_SUB_PATH)
    public ResponseEntity<ResponseAPI> createBranch(@RequestBody @Valid CreateBranchRequest body) {
        branchService.createBranch(body);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = BRANCH_UPDATE_BY_ID_SUM)
    @PutMapping(path = PUT_BRANCH_UPDATE_SUB_PATH)
    public ResponseEntity<ResponseAPI> updateBranchInfoById(@RequestBody @Valid UpdateBranchRequest body, @PathVariable(BRANCH_ID) String branchId) {
        branchService.updateBranchById(body, branchId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = BRANCH_DELETE_BY_ID_SUM)
    @DeleteMapping(path = DELETE_BRANCH_UPDATE_SUB_PATH)
    public ResponseEntity<ResponseAPI> deleteBranchInfoById(@PathVariable(BRANCH_ID) String branchId) {
        branchService.deleteBranchById(branchId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.DELETED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = BRANCH_GET_ALL_SUM)
    @GetMapping(path = GET_BRANCH_ALL_SUB_PATH)
    public ResponseEntity<ResponseAPI> getAllBranch() {
        List<BranchEntity> resData =  branchService.getBranchList();
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
