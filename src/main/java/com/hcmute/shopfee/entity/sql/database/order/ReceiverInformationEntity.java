package com.hcmute.shopfee.entity.sql.database.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.AddressEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.ReceiverInformationID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

@Entity
@Table(name = "receiver_information")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverInformationEntity {
    @Id
    @GenericGenerator(name = "receiver_information_id", type = RandomTimeGenerator.class)
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

    // =================================================================

    @OneToOne(mappedBy = "receiverInformation")
    @JsonManagedReference
    private OrderBillEntity orderBill;

    public void fromAddressEntity(AddressEntity address) {
        this.setNote(address.getNote());
        this.setAddress(address.getDetail());
        this.setLongitude(address.getLongitude());
        this.setLatitude(address.getLatitude());
        this.setPhoneNumber(address.getPhoneNumber());
        this.setRecipientName(address.getRecipientName());
    }
}
