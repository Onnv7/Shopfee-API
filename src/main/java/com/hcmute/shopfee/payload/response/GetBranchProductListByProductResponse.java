package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.entity.sql.database.product.BranchProductEntity;
import com.hcmute.shopfee.enums.BranchProductStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetBranchProductListByProductResponse {

    private int totalPage;
    private List<BranchProduct> branchProductList;

    @Data
    public static class BranchProduct {
        private String id;
        private String name;
        private BranchProductStatus branchProductStatus;

        private static BranchProduct fromBranchProductEntity(BranchProductEntity entity) {
            BranchProduct data = new BranchProduct();
            data.setId(entity.getId().getBranchId());
            data.setName(entity.getBranch().getName());
            data.setBranchProductStatus(entity.getStatus());
            return data;
        }
    }

    public static List<BranchProduct> fromBranchProductEntityList(List<BranchProductEntity> entityList) {
        List<BranchProduct> data = new ArrayList<>();
        for(BranchProductEntity entity : entityList) {
            data.add(BranchProduct.fromBranchProductEntity(entity));
        }
        return data;
    }

}
