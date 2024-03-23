package com.hcmute.shopfee.entity.sql.database.order;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "item_topping")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemToppingEntity {
    @Id
    @GenericGenerator(name = "order_event_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "order_event_id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;
    // =================================================================

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "item_detail_id", nullable = false)
    private ItemDetailEntity itemDetail;
}
