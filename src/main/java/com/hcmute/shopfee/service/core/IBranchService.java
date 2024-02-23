package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.CreateBranchRequest;
import com.hcmute.shopfee.dto.request.UpdateBranchRequest;
import com.hcmute.shopfee.entity.database.BranchEntity;

import java.util.List;

public interface IBranchService {
    void createBranch(CreateBranchRequest body);
    void updateBranchById(UpdateBranchRequest body, String id);
    void deleteBranchById(String id);
    List<BranchEntity> getBranchList();
}
