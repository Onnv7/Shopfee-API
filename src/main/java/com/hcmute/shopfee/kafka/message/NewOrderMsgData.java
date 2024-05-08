package com.hcmute.shopfee.kafka.message;

import com.hcmute.shopfee.constant.ShopfeeConstant;
import lombok.Data;

@Data
public class NewOrderMsgData {
    private String branchId;
    private String title;
    private String body;

    public NewOrderMsgData() {
    }
    public NewOrderMsgData(String branchId, String body) {
        this.branchId = branchId;
        this.title = ShopfeeConstant.EMPLOYEE_NOTI_TITLE_MSG;
        this.body = body;
    }
    public NewOrderMsgData(String branchId, String title, String body) {
        this.branchId = branchId;
        this.title = title;
        this.body = body;
    }
}
