package com.hcmute.shopfee.repository.database.product;

import com.hcmute.shopfee.entity.sql.database.product.SizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends JpaRepository<SizeEntity, String> {
}
