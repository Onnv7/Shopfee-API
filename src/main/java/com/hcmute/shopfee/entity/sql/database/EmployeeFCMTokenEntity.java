package com.hcmute.shopfee.entity.sql.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "fcm_token_employee")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFCMTokenEntity {
    @Id
    @GenericGenerator(name = "fcm_token_employee_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "fcm_token_employee_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference
    private EmployeeEntity employee;

    @Column(name = "token", nullable = false, unique = true)
    private String token;
}
