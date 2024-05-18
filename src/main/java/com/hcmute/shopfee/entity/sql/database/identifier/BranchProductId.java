package com.hcmute.shopfee.entity.sql.database.identifier;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class BranchProductId implements Serializable {
    @Column(name = "branch_id")
    private String branchId;
    @Column(name = "product_id")
    private String productId;

    public BranchProductId() {
    }

    public BranchProductId(String branchId, String productId) {
        this.branchId = branchId;
        this.productId = productId;
    }
}
