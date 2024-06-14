package com.hcmute.shopfee.entity.sql.database.identifier;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class BranchProductId implements Serializable {
    @JoinColumn(name = "branch_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private String branchId;
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private String productId;

    public BranchProductId() {
    }

    public BranchProductId(String branchId, String productId) {
        this.branchId = branchId;
        this.productId = productId;
    }
}
