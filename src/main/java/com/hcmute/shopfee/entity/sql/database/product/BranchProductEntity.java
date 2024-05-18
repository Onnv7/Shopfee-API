package com.hcmute.shopfee.entity.sql.database.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.BranchEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.BranchProductId;
import com.hcmute.shopfee.enums.BranchProductStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branch_product")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchProductEntity {
    @EmbeddedId
    private BranchProductId id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "branch_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BranchEntity branch;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private ProductEntity product;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BranchProductStatus status;
}
