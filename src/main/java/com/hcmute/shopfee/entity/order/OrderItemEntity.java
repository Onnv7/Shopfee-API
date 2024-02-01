package com.hcmute.shopfee.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.dto.common.OrderItemDto;
import com.hcmute.shopfee.entity.coupon_used.reward.ProductRewardReceivedEntity;
import com.hcmute.shopfee.entity.product.ProductEntity;
import com.hcmute.shopfee.enums.ProductSize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "order_item")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {
    @Id
    @GenericGenerator(name = "order_event_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "order_event_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "size")
    @Enumerated(EnumType.STRING)
    private ProductSize size;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "note")
    private String note;

//    @Column(name = "coupon_product_code")
//    private String couponProductCode;
//
//    private Long moneyDiscount;
//    private ProductGiftEmbedded productGift;
    @ManyToOne
    @JoinColumn(name = "order_bill_id", nullable = false)
    @JsonBackReference
    private OrderBillEntity orderBill;


    // =================================================================
    @OneToMany(mappedBy = "orderItem", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<ItemToppingEntity> itemToppingList;

//    @OneToMany(mappedBy = "orderItem")
//    @JsonManagedReference
//    private List<ProductRewardReceivedEntity> productRewardReceivedList;
    public void fromOrderItemDto(OrderItemDto orderItemDto) {
        this.setQuantity(orderItemDto.getQuantity());
        this.setNote(orderItemDto.getNote());
        this.setSize(orderItemDto.getSize());
    }
}
