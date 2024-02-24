package com.hcmute.shopfee.repository.elasticsearch;

import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductIndex, String> {
    @Query("""
        {
             "bool": {
                "must": [
                 { "match": { "isDeleted": false } }
                ],
               "must_not": [
                 { "match": { "status": "HIDDEN" } }
               ],
               "should": [
                 { "match": { "name": "?0 " } },
                 { "match": { "description": "?0" } },
                 { "regexp": { "id": "?1" } }
               ],
               "minimum_should_match": 1
             }
               
        }
    """)
    Page<ProductIndex> searchVisibleProduct(String key, String code, Pageable page);

    @Query("""
            {
                "bool": {
                   "must": [
                        { "multi_match": { "query": "?0", "fields": ["name", "description", "id"] }},
                        { "regexp": { "categoryId": "?1" } },
                        { "regexp": { "status": "?2" } },
                        { "match": { "isDeleted": false } }
                   ]
                }
            }
            """)
        // TODO: xem lại chỗ code có nên regex như category id không
    Page<ProductIndex> searchProduct(String key, String categoryIdRegex, String productStatusRegex, Pageable page);
}
