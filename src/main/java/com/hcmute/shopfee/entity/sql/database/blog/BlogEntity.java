package com.hcmute.shopfee.entity.sql.database.blog;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.employee.EmployeeEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.entity.sql.database.identifier.SeqIdentifierGenerator;
import com.hcmute.shopfee.entity.sql.database.identifier.StringPrefixedSequenceGenerator;
import com.hcmute.shopfee.enums.BlogStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "blog")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BlogEntity {
    @Id
    @GenericGenerator(name = "blog_id", type = SeqIdentifierGenerator.class, parameters = {
            @org.hibernate.annotations.Parameter(name = SeqIdentifierGenerator.ENTITY_NAME_PARAMETER, value = "BlogEntity"),
            @org.hibernate.annotations.Parameter(name = SeqIdentifierGenerator.VALUE_PREFIX_PARAMETER, value = "BLG"),
            @org.hibernate.annotations.Parameter(name = SeqIdentifierGenerator.NUMBER_FORMAT_PARAMETER, value = "%03d")
    })
    @GeneratedValue(generator = "blog_id")
    @Column(length = 7)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BlogStatus status;

    @Column(name = "cloudinary_image_id", nullable = false)
    private String cloudinaryImageId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "summary",  columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Column(name = "content",  columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "is_deleted",  columnDefinition = "BOOLEAN DEFAULT false", nullable = false)
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


    // =================================================
//    @OneToMany(mappedBy = "blog")
//    @JsonManagedReference
//    private List<BlogImageEntity> blogImageList;
}
