package com.hcmute.shopfee.entity.sql.database.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.ConfirmationEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.SeqIdentifierGenerator;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.Gender;
import com.hcmute.shopfee.enums.UserRole;
import com.hcmute.shopfee.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
//    @GenericGenerator(name = "user_id", type = IdGenerator.class)
    @Id
    @GenericGenerator(name = "user_id", type = SeqIdentifierGenerator.class, parameters = {
            @Parameter(name = SeqIdentifierGenerator.ENTITY_NAME_PARAMETER, value = "UserEntity"),
            @Parameter(name = SeqIdentifierGenerator.VALUE_PREFIX_PARAMETER, value = "U"),
            @Parameter(name = SeqIdentifierGenerator.NUMBER_FORMAT_PARAMETER, value = "%08d")
    })
    @GeneratedValue(generator = "user_id")
    @Column(length = 9)
    private String id;

    @Column(name = "avatar_id", unique = true)
    private String avatarId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth_date")
    private java.sql.Date birthDate;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // =================================================================
    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<AddressEntity> addressList;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<OrderBillEntity> orderBillList;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<CoinHistoryEntity> coinHistoryList;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<UserFCMTokenEntity> userFcmTokenList;

    @OneToOne(mappedBy = "user")
    @JsonManagedReference
    private ConfirmationEntity confirmation;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
