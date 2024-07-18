package com.hcmute.shopfee.kafka.message;

import com.hcmute.shopfee.constant.ShopfeeConstant;
import lombok.Data;

import java.util.Map;

@Data
public class NewOrderMsgData {
    private String branchId;
    private String title;
    private String body;
    private Map<String, String> data;

    public NewOrderMsgData() {
    }
    public NewOrderMsgData(String branchId, String body, Map<String, String> data) {
        this.branchId = branchId;
        this.title = ShopfeeConstant.EMPLOYEE_NOTI_TITLE_MSG;
        this.body = body;
        this.data = data;
    }
    public NewOrderMsgData(String branchId, String title, String body, Map<String, String> data) {
        this.branchId = branchId;
        this.title = title;
        this.body = body;
    }
}
