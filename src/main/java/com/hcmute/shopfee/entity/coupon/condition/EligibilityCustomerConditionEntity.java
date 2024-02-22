//package com.hcmute.shopfee.entity.coupon.condition;
//
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.hcmute.shopfee.entity.coupon.CouponConditionEntity;
//import com.hcmute.shopfee.enums.ApplicableCustomerType;
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.GenericGenerator;
//
//import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;
//
//@Entity
//@Table(name = "applicable_customer_condition")
//@Builder
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class EligibilityCustomerConditionEntity {
//    @Id
//    @GenericGenerator(name = "applicable_customer_condition_id", strategy = TIME_ID_GENERATOR)
//    @GeneratedValue(generator = "applicable_customer_condition_id")
//    private String id;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "type", nullable = false)
//    private ApplicableCustomerType type;
//
//    @Column(name = "value")
//    private String value;
//
//    @OneToOne
//    @JoinColumn(name = "coupon_condition_id", nullable = false)
//    @JsonBackReference
//    private CouponConditionEntity couponCondition;
//
//}
