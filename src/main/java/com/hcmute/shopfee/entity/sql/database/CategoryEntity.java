package com.hcmute.shopfee.entity.sql.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.identifier.StringPrefixedSequenceGenerator;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

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
    @GenericGenerator(name = "category_id", type = StringPrefixedSequenceGenerator.class, parameters = {
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceGenerator.INCREMENT_PARAM, value = "1"),
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceGenerator.VALUE_PREFIX_PARAMETER, value = "C"),
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceGenerator.NUMBER_FORMAT_PARAMETER, value = "%02d")
    })
    @GeneratedValue(generator = "category_id")
    private String id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "image_id")
    @JsonBackReference
    private AlbumEntity image;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(255) default 'HIDDEN'")
    private CategoryStatus status = CategoryStatus.HIDDEN;


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
