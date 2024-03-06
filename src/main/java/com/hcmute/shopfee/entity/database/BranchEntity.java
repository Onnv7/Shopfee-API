package com.hcmute.shopfee.entity.database;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.BranchStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "branch")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "branch_id")
    @SequenceGenerator(name = "branch_id", sequenceName = "branch_id", initialValue = 1, allocationSize = 1)
    private String id;

    @Column(nullable = false)
    private String province;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String ward;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double latitude;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "open_time", nullable = false)
    private Time openTime;

    @Column(name = "close_time", nullable = false)
    private Time closeTime;

    @Column(name = "close_time", nullable = false)
    private BranchStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    // =================================================

    @OneToMany(mappedBy = "branch")
    @JsonManagedReference
    private List<OrderBillEntity> orderBillList;

    @OneToMany(mappedBy = "branch")
    @JsonManagedReference
    private List<EmployeeEntity> employeeList;

    public String getFullAddress() {
        return this.getDetail() + " " + this.getWard() + " " + this.getDistrict() + " " + this.getProvince();
    }

    public String getOpenTimeOfBranch() {
        return this.getOpenTime() + " - " + this.getCloseTime();
    }
}
