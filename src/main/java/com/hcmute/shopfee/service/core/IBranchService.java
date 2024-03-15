package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.CreateBranchRequest;
import com.hcmute.shopfee.dto.request.UpdateBranchRequest;
import com.hcmute.shopfee.dto.response.GetAllBranchResponse;
import com.hcmute.shopfee.dto.response.GetBranchDetailByIdResponse;
import com.hcmute.shopfee.dto.response.GetBranchViewByIdResponse;
import com.hcmute.shopfee.dto.response.GetBranchViewListResponse;
import com.hcmute.shopfee.entity.database.BranchEntity;

import java.util.List;

public interface IBranchService {
    void createBranch(CreateBranchRequest body);
    void updateBranchById(UpdateBranchRequest body, String id);
    void deleteBranchById(String id);
    GetAllBranchResponse getBranchList(int page, int size);
    GetBranchDetailByIdResponse getBranchDetailById(String branchId);
    GetBranchViewByIdResponse getBranchViewById(String branchId);
    GetBranchViewListResponse getBranchViewList(Double latitude, Double longitude, String key, int page, int size);
}
