package com.hcmute.shopfee.service.elasticsearch;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.repository.elasticsearch.ProductESRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Kind.Terms;

@Service
@RequiredArgsConstructor
public class ProductESService {
    private final ProductESRepository productESRepository;
    @Autowired
    @Lazy
    private ProductRepository productRepository;

    public void syncProductIndexAndDatabase() {
        productESRepository.deleteAll();
        List<ProductEntity> productEntityList = productRepository.findAll();
        for (ProductEntity productEntity : productEntityList) {
            createProduct(productEntity);
        }
    }
    public ProductIndex createProduct(ProductEntity data) {

        ProductIndex dataSearch = ProductIndex.builder()
                .id(data.getId())
                .name(data.getName())
                .thumbnailUrl(data.getImage().getThumbnailUrl())
                .description(data.getDescription())
                .status(data.getStatus())
                .categoryId(data.getCategory().getId())
                .type(data.getType())
                .price(data.getPrice())
                .build();

        return productESRepository.save(dataSearch);
    }

    public void upsertProduct(ProductEntity data) {
        ProductIndex product = productESRepository.findById(data.getId()).orElse(null);
        if (product != null) {
            product.setName(data.getName());
            product.setThumbnailUrl(data.getImage().getThumbnailUrl());
            product.setStatus(data.getStatus());
            product.setPrice(data.getPrice());
            product.setDescription(data.getDescription());
            product.setCategoryId(data.getCategory().getId());
            productESRepository.save(product);
        } else {
            createProduct(data);
        }
    }

    public void deleteProduct(String id) {
        ProductIndex productIndex = productESRepository.findById(id)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.PRODUCT_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + id));
        productESRepository.delete(productIndex);
    }

    public Page<ProductIndex> searchVisibleProduct(String key,  Pageable pageable) {
//        Pageable pageable = PageRequest.of(page - 1, size);
//        String textRegex = RegexUtils.generateFilterRegexString(key);
        return productESRepository.searchVisibleProduct(key, pageable);
    }

    public Page<ProductIndex> searchProduct(String key, String categoryIdRegex, String productStatusRegex, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productESRepository.searchProduct(key, categoryIdRegex, productStatusRegex, pageable);
    }

    @Autowired
    private RestHighLevelClient client;
    public List<String> getAutoCompleteSuggestions(String key) throws IOException {
        SearchRequest searchRequest = new SearchRequest("product");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // Tạo query bool với wildcard query
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.wildcardQuery("name", key + "*")));

        // Tạo aggregation để lấy các term từ field name.keyword
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("auto_complete")
                        .field("name.keyword")
                        .size(10)
                        .order(BucketOrder.count(false))
        );

        // Bỏ qua các field _source
        searchSourceBuilder.fetchSource(false);

        // Thiết lập kích thước của kết quả trả về là 0 vì chúng ta chỉ quan tâm đến aggregation
        searchSourceBuilder.size(0);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        // Xử lý kết quả aggregation
        Aggregation autoCompleteAggregation = searchResponse.getAggregations().get("auto_complete");

        return ((ParsedStringTerms) autoCompleteAggregation).getBuckets().stream().map(it -> it.getKey().toString()).toList();
    }
}
