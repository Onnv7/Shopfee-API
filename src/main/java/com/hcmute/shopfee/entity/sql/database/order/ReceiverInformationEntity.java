package com.hcmute.shopfee.entity.sql.database.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.AddressEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "receiver_information")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverInformationEntity {
    @Id
    @GenericGenerator(name = "receiver_information_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "receiver_information_id")
    private String id;

    @Column(name = "address")
    private String address;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "note")
    private String note;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "receive_time")
    private Date receiveTime;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToOne()
    @JoinColumn(name = "order_bill_id", nullable = false)
    @JsonBackReference
    private OrderBillEntity orderBill;
    // =================================================================

    public void fromAddressEntity(AddressEntity address) {
        this.setNote(address.getNote());
        this.setAddress(address.getDetail());
        this.setLongitude(address.getLongitude());
        this.setLatitude(address.getLatitude());
        this.setPhoneNumber(address.getPhoneNumber());
        this.setRecipientName(address.getRecipientName());
    }
}
