package com.hcmute.shopfee.entity.sql.database.identifier;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ReceiverInformationID {
    @Column(name = "order_bill_id", nullable = false)
    private String orderBillId;
}
