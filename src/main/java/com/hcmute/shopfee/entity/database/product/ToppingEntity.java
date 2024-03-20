package com.hcmute.shopfee.entity.database.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.dto.common.ToppingDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "topping")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToppingEntity {
    @Id
    @GenericGenerator(name = "size_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "size_id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false, columnDefinition = "BIGINT CHECK (price >= 1000)")
    private Long price;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_id", nullable = false)
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
