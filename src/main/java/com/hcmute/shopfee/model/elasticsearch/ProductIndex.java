package com.hcmute.shopfee.model.elasticsearch;

import com.hcmute.shopfee.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Setting(settingPath = "/config/elasticsearch/product/setting.json")
@Mapping(mappingPath = "/config/elasticsearch/product/mapping.json")
@Document(indexName = "product")
public class ProductIndex {
    @Id
    private String id;
    private String code;
    private String name;
    private String thumbnailUrl;
    private String description;
    private double price;
    private ProductStatus status;
    private String categoryId;
    private boolean isDeleted;
}
