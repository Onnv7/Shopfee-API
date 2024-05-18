package com.hcmute.shopfee.entity.sql.database.order;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "item_topping")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemToppingEntity {
    @Id
    @GenericGenerator(name = "order_event_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "order_event_id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "item_detail_id", nullable = false)
    private ItemDetailEntity itemDetail;
    // =================================================================


}
