package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.CreateBranchRequest;
import com.hcmute.shopfee.dto.request.UpdateBranchRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.entity.database.BranchEntity;

import java.sql.Time;
import java.util.List;

public interface IBranchService {
    void createBranch(CreateBranchRequest body);
    void updateBranchById(UpdateBranchRequest body, String id);
    void deleteBranchById(String id);
    GetAllBranchResponse getBranchList(int page, int size);
    GetBranchNearestResponse getBranchNearest(Double latitude, Double longitude, Time time);
    GetBranchDetailByIdResponse getBranchDetailById(String branchId);
    GetBranchViewByIdResponse getBranchViewById(String branchId);
    GetBranchViewListResponse getBranchViewList(boolean isGetAll, Double latitude, Double longitude, String key, int page, int size);
}
