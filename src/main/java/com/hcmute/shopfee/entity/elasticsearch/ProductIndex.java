package com.hcmute.shopfee.entity.elasticsearch;

import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
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
@Setting(settingPath = "/elasticsearch/product/setting.json")
@Mapping(mappingPath = "/elasticsearch/product/mapping.json")
@Document(indexName = "product")
public class ProductIndex {
    @Id
    private String id;
//    private String code;
    private String name;
    private ProductType type;
    private String thumbnailUrl;
    private String description;
    private double price;
    private ProductStatus status;
    private String categoryId;
}
