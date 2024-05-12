package com.hcmute.shopfee.repository.elasticsearch;

import com.hcmute.shopfee.entity.elasticsearch.TrackingUserProductIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingUserClickProductRepository extends ElasticsearchRepository<TrackingUserProductIndex, String> {
    Optional<TrackingUserProductIndex> findByUserIdAndProductId(String userId, String productId);
    Page<TrackingUserProductIndex> findByUserId(String userId, Pageable pageable);
}
