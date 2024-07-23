package com.hcmute.shopfee.entity.sql.database.employee;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.admin.BannerEntity;
import com.hcmute.shopfee.entity.sql.database.admin.BranchEntity;
import com.hcmute.shopfee.entity.sql.database.blog.BlogEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.SeqIdentifierGenerator;
import com.hcmute.shopfee.enums.EmployeeRole;
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
import java.util.List;

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
//    @GenericGenerator(name = "employee_id", type = IdGenerator.class)
    @GenericGenerator(name = "employee_id", type = SeqIdentifierGenerator.class, parameters = {
            @Parameter(name = SeqIdentifierGenerator.VALUE_PREFIX_PARAMETER, value = "E"),
            @Parameter(name = SeqIdentifierGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d"),
            @Parameter(name = SeqIdentifierGenerator.ENTITY_NAME_PARAMETER, value = "EmployeeEntity")
    })
    @GeneratedValue(generator = "employee_id")
    @Column(length = 6)
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

    @Column(name = "email")
    private String email;

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
    @Column(name = "updated_at")
    private Date updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeRole role;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonBackReference
    private BranchEntity branch;

    // =================================================================================================
    @OneToMany(mappedBy = "employee")
    @JsonManagedReference
    private List<EmployeeFCMTokenEntity> employeeFcmTokenList;

    @OneToMany(mappedBy = "employee")
    @JsonManagedReference
    private List<BannerEntity> bannerList;

    @OneToMany(mappedBy = "employee")
    @JsonManagedReference
    private List<BlogEntity> blogList;


    public String getFullName() {
        return this.firstName + " " + this.getLastName();
    }

}
