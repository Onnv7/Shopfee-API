package com.hcmute.shopfee.entity.sql.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

@Entity
@Table(name = "fcm_token_employee")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFCMTokenEntity {
    @Id
    @GenericGenerator(name = "fcm_token_employee_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "fcm_token_employee_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference
    private EmployeeEntity employee;

    @Column(name = "token", nullable = false, unique = true)
    private String token;
}
