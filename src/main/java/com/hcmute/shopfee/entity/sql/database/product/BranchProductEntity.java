package com.hcmute.shopfee.entity.sql.database.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.BranchEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.BranchProductId;
import com.hcmute.shopfee.enums.BranchProductStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branch_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchProductEntity {
    @EmbeddedId
    private BranchProductId id = new BranchProductId();

    @ManyToOne
    @MapsId("branchId")
    @JsonBackReference
    private BranchEntity branch;

    @ManyToOne
    @MapsId("productId")
    @JsonBackReference
    private ProductEntity product;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BranchProductStatus status;
}
