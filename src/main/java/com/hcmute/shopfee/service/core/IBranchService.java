package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.payload.request.CreateBranchRequest;
import com.hcmute.shopfee.payload.request.UpdateBranchRequest;
import com.hcmute.shopfee.payload.response.*;

import java.sql.Time;
import java.util.concurrent.ExecutionException;

public interface IBranchService {
    void createBranch(CreateBranchRequest body) throws ExecutionException, InterruptedException;
    void updateBranchById(UpdateBranchRequest body, String id);
    void deleteBranchById(String branchId);
    GetAllBranchResponse getBranchList(int page, int size);
    GetBranchNearestResponse getBranchNearest(Double latitude, Double longitude, Time time);
    GetBranchDetailByIdResponse getBranchDetailById(String branchId);
    GetBranchViewByIdResponse getBranchViewById(String branchId);
    GetBranchViewListResponse getBranchViewList(boolean isGetAll, Double latitude, Double longitude, String key, int page, int size);
}
