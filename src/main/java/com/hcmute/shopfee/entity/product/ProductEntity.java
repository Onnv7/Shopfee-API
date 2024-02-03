package com.hcmute.shopfee.entity.product;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.CategoryEntity;
import com.hcmute.shopfee.entity.identifier.StringPrefixedSequenceGenerator;
import com.hcmute.shopfee.entity.order.OrderItemEntity;
import com.hcmute.shopfee.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.SEQUENCE_ID_GENERATOR;
import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "product")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ProductEntity {
    @Id
    @GenericGenerator(name = "product_id", strategy = SEQUENCE_ID_GENERATOR, parameters = {
            @Parameter(name = StringPrefixedSequenceGenerator.INCREMENT_PARAM, value = "1"),
            @Parameter(name = StringPrefixedSequenceGenerator.VALUE_PREFIX_PARAMETER, value = "P"),
            @Parameter(name = StringPrefixedSequenceGenerator.NUMBER_FORMAT_PARAMETER, value = "%04d")
    })
    @GeneratedValue(generator = "product_id", strategy = GenerationType.SEQUENCE)
    private String id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "image_id", unique = true, nullable = false)
    private String imageId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @Column(name = "price", nullable = false)
    private Long price;


    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    //    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(255) default 'HIDDEN'")
    private ProductStatus status;


    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "update_at")
    private Date updateAt;

    // =================================================================
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<SizeEntity> sizeList;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<ToppingEntity> toppingList;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<OrderItemEntity> orderItemList;



}
