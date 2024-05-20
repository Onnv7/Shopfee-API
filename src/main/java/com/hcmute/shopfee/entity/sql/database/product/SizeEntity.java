package com.hcmute.shopfee.entity.sql.database.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.dto.common.SizeDto;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.entity.sql.database.identifier.SizeID;
import com.hcmute.shopfee.enums.ProductSize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "size")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(SizeID.class)
public class SizeEntity {
    @Id
    @Column(name = "size", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductSize size;

    @Column(name = "price", nullable = false, columnDefinition = "BIGINT CHECK (price >= 1000)")
    private Long price;

    @Id
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
