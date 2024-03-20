package com.hcmute.shopfee.entity.database;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.database.product.ProductEntity;
import com.hcmute.shopfee.enums.AlbumType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "album")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AlbumEntity {
    @Id
    @GenericGenerator(name = "album_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "album_id")
    private String id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AlbumType type;

    @Column(name = "cloudinary_image_id")
    private String cloudinaryImageId;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    // ======================================================

    @OneToOne(mappedBy = "image")
    @JsonManagedReference
    private ProductEntity product;

    @OneToOne(mappedBy = "image")
    @JsonManagedReference
    private CategoryEntity category;

}
