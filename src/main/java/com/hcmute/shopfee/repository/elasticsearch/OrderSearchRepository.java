package com.hcmute.shopfee.repository.elasticsearch;

import com.hcmute.shopfee.entity.elasticsearch.OrderIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderSearchRepository extends ElasticsearchRepository<OrderIndex, String> {

    @Query("""
        {
          "bool": {
            "must": [
              {"regexp": {"statusLastEvent": "?1"}}
            ],
            "should": [
              {"match": {"code": "?0"}},
              {"match": {"customerCode": "?0"}},
              {"match": {"email": "?0"}},
              {"match": {"phoneNumber": "?0"}},
              {"match": {"phoneNumberReceiver": "?0"}}
            ],
            "minimum_should_match": 1
          }
        }
        """)
    Page<OrderIndex> searchOrderForAdmin(String key, String statusFilter, Pageable page);

}
