package com.hcmute.shopfee.entity.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.database.identifier.StringPrefixedSequenceGenerator;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.enums.Gender;
import jakarta.persistence.*;
import org.hibernate.annotations.Parameter;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.Set;

import static com.hcmute.shopfee.constant.EntityConstant.SEQUENCE_ID_GENERATOR;

@Entity
@Table(name = "employee")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class EmployeeEntity {
    @Id
//    @GenericGenerator(name = "employee_id", strategy = TIME_ID_GENERATOR)
    @GenericGenerator(name = "employee_id", strategy = SEQUENCE_ID_GENERATOR, parameters = {
            @Parameter(name = StringPrefixedSequenceGenerator.INCREMENT_PARAM, value = "1"),
            @Parameter(name = StringPrefixedSequenceGenerator.VALUE_PREFIX_PARAMETER, value = "E"),
            @Parameter(name = StringPrefixedSequenceGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
    })
    @GeneratedValue(generator = "employee_id")
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth_date")
    private java.sql.Date birthDate;

    @Column(name = "phone_number")
    private String phoneNumber;


//    private ObjectId branchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmployeeStatus status;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted ;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "update_at")
    private Date updateAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "employee_role",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roleList;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonBackReference
    private BranchEntity branch;
}
