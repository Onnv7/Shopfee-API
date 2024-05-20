package com.hcmute.shopfee.entity.sql.database.identifier;

import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class SizeID {
    private ProductEntity product;
    private String size;
}
