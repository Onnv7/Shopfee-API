package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.order.CancellationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancellationDemandRepository extends JpaRepository<CancellationRequestEntity, String> {
}
