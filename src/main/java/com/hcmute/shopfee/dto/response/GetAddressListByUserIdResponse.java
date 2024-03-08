package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.database.AddressEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetAddressListByUserIdResponse {
    private String id;
    private String detail;
    private String recipientName;
    private boolean isDefault;
    private String phoneNumber;
    private static GetAddressListByUserIdResponse fromAddressEntity(AddressEntity entity) {
        GetAddressListByUserIdResponse data = new GetAddressListByUserIdResponse();
        data.setId(entity.getId());
        data.setDetail(entity.getDetail());
        data.setRecipientName(entity.getRecipientName());
        data.setPhoneNumber(entity.getPhoneNumber());
        data.setDefault(entity.isDefault());
        return data;
    }
    public static List<GetAddressListByUserIdResponse> fromAddressEntityList(List<AddressEntity> entityList) {
        List<GetAddressListByUserIdResponse> data = new ArrayList<>();
        entityList.forEach(entity -> data.add(fromAddressEntity(entity)));
        return data;
    }
}
