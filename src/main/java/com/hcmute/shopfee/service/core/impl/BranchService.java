package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.CreateBranchRequest;
import com.hcmute.shopfee.dto.request.UpdateBranchRequest;
import com.hcmute.shopfee.entity.database.BranchEntity;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.BranchRepository;
import com.hcmute.shopfee.service.core.IBranchService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService implements IBranchService {
    private final BranchRepository branchRepository;
    private final ModelMapperService modelMapperService;

    @Override
    public void createBranch(CreateBranchRequest body) {
        BranchEntity branch = modelMapperService.mapClass(body, BranchEntity.class);
        branchRepository.save(branch);
    }

    @Override
    public void updateBranchById(UpdateBranchRequest body, String id) {
        BranchEntity branch = branchRepository.findById(id).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));
        modelMapperService.map(body, branch);
        branchRepository.save(branch);
    }

    // TODO: branch hiện đăng xóa cứng
    @Override
    public void deleteBranchById(String id) {
        if (branchRepository.existsById(id)) {
            branchRepository.deleteById(id);
        } else {
            throw new CustomException(ErrorConstant.NOT_FOUND + id);
        }
    }

    @Override
    public List<BranchEntity> getBranchList() {
        return branchRepository.findAll();
    }
}
