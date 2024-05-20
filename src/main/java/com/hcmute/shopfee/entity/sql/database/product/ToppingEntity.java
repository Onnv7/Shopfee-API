package com.hcmute.shopfee.entity.sql.database.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.dto.common.ToppingDto;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.entity.sql.database.identifier.ToppingID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topping")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ToppingID.class)
public class ToppingEntity {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false, columnDefinition = "BIGINT CHECK (price >= 1000)")
    private Long price;

    @Id
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_id", nullable = false, insertable = false, updatable = false)
    private ProductEntity product;

    public static List<ToppingEntity> fromToppingDtoList(List<ToppingDto> toppingDtoList, ProductEntity product) {
        List<ToppingEntity> result = new ArrayList<>();
        for (ToppingDto toppingDto: toppingDtoList) {
            ToppingEntity toppingEntity = new ToppingEntity();
            toppingEntity.setName(toppingDto.getName());
            toppingEntity.setPrice(toppingDto.getPrice());
            toppingEntity.setProduct(product);
            result.add(toppingEntity);
        }
        return result;
    }

}
