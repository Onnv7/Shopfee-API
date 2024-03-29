package com.hcmute.shopfee.entity.sql.database.product;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.AlbumEntity;
import com.hcmute.shopfee.entity.sql.database.CategoryEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.StringPrefixedSequenceGenerator;
import com.hcmute.shopfee.entity.sql.database.order.OrderItemEntity;
import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.SEQUENCE_ID_GENERATOR;

@Entity
@Table(name = "product")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    @Column(nullable = false)
    private String name;

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

    //    private boolean enabled = true;

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
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JsonManagedReference
    @ToString.Exclude
    private List<SizeEntity> sizeList;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JsonManagedReference
    @ToString.Exclude
    private List<ToppingEntity> toppingList;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    @ToString.Exclude
    private List<OrderItemEntity> orderItemList;


}
