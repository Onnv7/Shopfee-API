package com.hcmute.shopfee.repository.elasticsearch;

import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import com.hcmute.shopfee.entity.elasticsearch.RatingProductIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingProductRepository extends ElasticsearchRepository<RatingProductIndex, String> {
}
