package com.hcmute.shopfee.entity.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(indexName = "rating_product")
public class RatingProductIndex {
    @Id
    private String id;
    @Field
    private String userId;
    @Field
    private String productId;
    @Field
    private Integer rating;
}
