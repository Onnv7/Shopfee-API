package com.hcmute.shopfee.service;

import com.hcmute.shopfee.dto.request.CreateBranchRequest;
import com.hcmute.shopfee.dto.request.UpdateBranchRequest;
import com.hcmute.shopfee.entity.BranchEntity;

import java.util.List;

public interface IBranchService {
    void createBranch(CreateBranchRequest body);
    void updateBranchById(UpdateBranchRequest body, String id);
    void deleteBranchById(String id);
    List<BranchEntity> getBranchList();
}
