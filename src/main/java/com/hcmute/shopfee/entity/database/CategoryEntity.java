package com.hcmute.shopfee.entity.database;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.database.product.ProductEntity;
import com.hcmute.shopfee.enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "category")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CategoryEntity {
    @Id
    @GenericGenerator(name = "category_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "category_id")
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_id", nullable = false)
    private String imageId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(255) default 'HIDDEN'")
    private CategoryStatus status = CategoryStatus.HIDDEN;


    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isDeleted = false;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

// =================================================================
    @OneToMany(mappedBy = "category")
    @JsonManagedReference
    private List<ProductEntity> productList;

}
