package com.hcmute.shopfee.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.AddressEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "shipping_information")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingInformationEntity {
    @Id
    @GenericGenerator(name = "shipping_information_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "shipping_information_id")
    private String id;

    @Column(name = "detail", nullable = false)
    private String detail;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "note")
    private String note;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToOne()
    @JoinColumn(name = "order_bill_id", nullable = false)
    @JsonBackReference
    private OrderBillEntity orderBill;
    // =================================================================

    public void fromAddressEntity(AddressEntity address) {
        this.setNote(address.getNote());
        this.setDetail(address.getDetail());
        this.setLongitude(address.getLongitude());
        this.setLatitude(address.getLatitude());
        this.setPhoneNumber(address.getPhoneNumber());
        this.setRecipientName(address.getRecipientName());
    }
}
