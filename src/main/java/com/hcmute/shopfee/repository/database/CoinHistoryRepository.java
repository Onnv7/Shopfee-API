package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.CoinHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinHistoryRepository extends JpaRepository<CoinHistoryEntity, String> {
    Page<CoinHistoryEntity> findByUser_Id(String userId, Pageable pageable);

    @Query(value = """
            select sum(ch.coin)
            from coin_history ch
            join user u on u.id = ch.user_id
            """, nativeQuery = true)
    long getCoinOfUser(String userId);
}
