package com.hcmute.shopfee.entity.sql.database.identifier;

import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.ProductSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SizeID {
    private ProductEntity product;
    private ProductSize size;
}
