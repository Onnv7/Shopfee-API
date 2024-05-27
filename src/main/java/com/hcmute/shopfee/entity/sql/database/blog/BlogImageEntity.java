//package com.hcmute.shopfee.entity.sql.database.blog;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.GenericGenerator;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//@Entity
//@Table(name = "blog_image")
//@Builder
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@EntityListeners(AuditingEntityListener.class)
//public class BlogImageEntity {
//    @Id
//    @GenericGenerator(name = "address_id", type = RandomTimeGenerator.class)
//    @GeneratedValue(generator = "address_id")
//    private String id;
//
//    @Column(name = "image_url", nullable = false)
//    private String imageUrl;
//
//    @ManyToOne
//    @JoinColumn(name = "blog_id", nullable = false)
//    @JsonBackReference
//    private BlogEntity blog;
//}
