package com.hcmute.shopfee.repository.database.product;

import com.hcmute.shopfee.entity.sql.database.product.ToppingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToppingRepository extends JpaRepository<ToppingEntity, String> {
}
