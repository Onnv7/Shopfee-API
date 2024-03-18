package com.hcmute.shopfee.entity.database;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.database.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "album")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumEntity {
    @Id
    @GenericGenerator(name = "album_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "album_id")
    private String id;

    @Column(unique = true, name = "image_url")
    private String imageUrl;

    @Column(unique = true, name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "cloudinary_image_id")
    private String cloudinaryImageId;

//    @OneToOne(mappedBy = "album")
//    @JsonManagedReference
//    private ProductEntity product;
}
