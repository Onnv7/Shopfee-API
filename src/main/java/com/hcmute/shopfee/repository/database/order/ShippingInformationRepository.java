package com.hcmute.shopfee.repository.database.order;

import com.hcmute.shopfee.entity.sql.database.order.ReceiverInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingInformationRepository extends JpaRepository<ReceiverInformationEntity, String> {
}
