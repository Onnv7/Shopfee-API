package com.hcmute.shopfee.repository.database.order;

import com.hcmute.shopfee.entity.database.order.ShippingInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingInformationRepository extends JpaRepository<ShippingInformationEntity, String> {
}
