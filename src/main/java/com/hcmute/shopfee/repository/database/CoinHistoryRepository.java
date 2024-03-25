package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.CoinHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinHistoryRepository extends JpaRepository<CoinHistoryEntity, String> {
}
