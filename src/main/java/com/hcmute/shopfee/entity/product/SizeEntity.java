package com.hcmute.shopfee.entity.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.dto.common.SizeDto;
import com.hcmute.shopfee.dto.common.ToppingDto;
import com.hcmute.shopfee.entity.product.ProductEntity;
import com.hcmute.shopfee.enums.ProductSize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "size")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SizeEntity {
    @Id
    @GenericGenerator(name = "size_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "size_id")
    private String id;

    @Column(name = "size", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductSize size;

    @Column(name = "price", nullable = false)
    private Long price;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    public static List<SizeEntity> fromToppingDtoList(List<SizeDto> sizeDtoList, ProductEntity product) {
        List<SizeEntity> result = new ArrayList<>();
        for (SizeDto sizeDto: sizeDtoList) {
            SizeEntity sizeEntity = new SizeEntity();
            sizeEntity.setSize(sizeDto.getSize());
            sizeEntity.setPrice(sizeDto.getPrice());
            sizeEntity.setProduct(product);
            result.add(sizeEntity);
        }
        return result;
    }
}
