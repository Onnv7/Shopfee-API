package com.hcmute.shopfee.entity.sql.database.admin;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.employee.EmployeeEntity;
import com.hcmute.shopfee.enums.BannerStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

@Entity
@Table(name = "banner")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BannerEntity {
    @Id
    @GenericGenerator(name = "banner_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "banner_id")
    @Column(length = 16)
    private String id;

    @Column(unique = true)
    private String name;

    @Column(unique = true, name = "cloudinary_image_id", nullable = false)
    private String cloudinaryImageId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BannerStatus status;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonBackReference
    private EmployeeEntity employee;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;
}
