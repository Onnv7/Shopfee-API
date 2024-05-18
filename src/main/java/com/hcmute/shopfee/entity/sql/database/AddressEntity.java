package com.hcmute.shopfee.entity.sql.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "address")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity {
    @Id
    @GenericGenerator(name = "address_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "address_id")
    private String id;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

    @Column(name = "note")
    private String note;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private UserEntity user;

    @Column(name = "is_default", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isDefault;
}
