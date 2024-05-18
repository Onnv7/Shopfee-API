package com.hcmute.shopfee.entity.sql.database.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

@Entity
@Table(name = "order_refund_media")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderRefundMediaEntity {
    @Id
    @GenericGenerator(name = "order_refund_media_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "order_refund_media_id")
    private String id;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;


    @Column(name = "cloudinary_media_id")
    private String cloudinaryMediaId;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne(cascade = {})
    @JsonBackReference
    @JoinColumn(name = "order_refund_request_id", nullable = false)
    private OrderRefundRequestEntity orderRefundRequest;
}
