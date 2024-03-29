package com.hcmute.shopfee.entity.elasticsearch;


import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
//@Setting(settingPath = "/elasticsearch/order/setting.json")
@Mapping(mappingPath = "/elasticsearch/order/mapping.json")
@Document(indexName = "order")
public class OrderIndex {
    @Id
    private String id;
//    private String code;
    private String customerName;
    private String email;
    private String customerCode;
    private String phoneNumber;
//    private String phoneNumberReceiver;
    private String recipientName;
    private String productName;
    private int productQuantity;
    private String productThumbnail;
    private Date timeLastEvent;
    private Date createdAt;
    private long total;
    private OrderStatus statusLastEvent;
    private OrderType orderType;
}
