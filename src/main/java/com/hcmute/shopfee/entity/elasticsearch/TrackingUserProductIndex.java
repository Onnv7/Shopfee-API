package com.hcmute.shopfee.entity.elasticsearch;

import com.hcmute.shopfee.enums.ProductStatus;
import com.hcmute.shopfee.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(indexName = "tracking_user_click_product")
public class TrackingUserProductIndex {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Keyword)
    private String userId;

    @Field(type = FieldType.Keyword)
    private String productId;

    @Field(type = FieldType.Integer)
    private Integer clickCount;

    @Field(type = FieldType.Date)
    private Date lastSeen;
}
