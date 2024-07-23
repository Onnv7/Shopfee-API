package com.hcmute.shopfee.entity.sql.database.product;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.admin.AlbumEntity;
import com.hcmute.shopfee.entity.sql.database.admin.CategoryEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.ProductRewardEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.reward.ProductRewardReceivedEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.SeqIdentifierGenerator;
import com.hcmute.shopfee.entity.sql.database.order.OrderItemEntity;
import com.hcmute.shopfee.entity.sql.listener.ProductListener;
import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "product")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class, ProductListener.class})
public class ProductEntity {
    @Id
    @GenericGenerator(name = "product_id", type = SeqIdentifierGenerator.class, parameters = {
            @Parameter(name = SeqIdentifierGenerator.ENTITY_NAME_PARAMETER, value = "ProductEntity"),
            @Parameter(name = SeqIdentifierGenerator.VALUE_PREFIX_PARAMETER, value = "P"),
            @Parameter(name = SeqIdentifierGenerator.NUMBER_FORMAT_PARAMETER, value = "%04d")
    })
    @GeneratedValue(generator = "product_id", strategy = GenerationType.SEQUENCE)
    @Column(length = 5)
    private String id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ProductType type;

    @Column(name = "price", nullable = false, columnDefinition = "BIGINT CHECK (price >= 1000)")
    private Long price;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "image_id")
    @JsonBackReference
    private AlbumEntity image;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(255) default 'HIDDEN'")
    private ProductStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    // =================================================================
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JsonManagedReference
//    @ToString.Exclude
    private List<SizeEntity> sizeList;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JsonManagedReference
//    @ToString.Exclude
    private List<ToppingEntity> toppingList;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    @ToString.Exclude
    private List<OrderItemEntity> orderItemList;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.REMOVE})
    @JsonManagedReference
    private List<BranchProductEntity> branchProductList;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<ProductRewardEntity> productRewardList;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<ProductRewardReceivedEntity> productRewardReceivedList;

    public BranchProductEntity getBranchProduct(String branchId) {
        return this.branchProductList.stream().filter(it -> it.getBranch().getId().equals(branchId)).findFirst()
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BRANCH_NOT_FOUND));
    }

}
