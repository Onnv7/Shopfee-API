package com.hcmute.shopfee.dto.common;

import com.hcmute.shopfee.entity.sql.database.BranchEntity;
import lombok.Data;

@Data
public class BranchDistanceDto {
    private BranchEntity branch;
    private Integer distance;

    public BranchDistanceDto() {
    }

    public BranchDistanceDto(BranchEntity branch, Integer distance) {
        this.branch = branch;
        this.distance = distance;
    }
}
