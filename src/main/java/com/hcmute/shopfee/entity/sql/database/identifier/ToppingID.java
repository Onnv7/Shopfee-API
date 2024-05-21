package com.hcmute.shopfee.entity.sql.database.identifier;

import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ToppingID {
    private ProductEntity product;
    private String name;
}
