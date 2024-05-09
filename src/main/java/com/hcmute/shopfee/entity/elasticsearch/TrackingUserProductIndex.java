package com.hcmute.shopfee.entity.elasticsearch;

import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(indexName = "tracking_user_click_product")
public class TrackingUserProductIndex {
    @Id
    private String id;
    @Field
    private String userId;
    @Field
    private String productId;
    @Field
    private Integer clickCount;
    @Field
    private Date lastSeen;
}
