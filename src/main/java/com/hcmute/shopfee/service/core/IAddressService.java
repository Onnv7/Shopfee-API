package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.CreateAddressRequest;
import com.hcmute.shopfee.dto.request.UpdateAddressRequest;
import com.hcmute.shopfee.dto.response.GetAddressDetailsByIdResponse;
import com.hcmute.shopfee.dto.response.GetAddressListByUserIdResponse;
import com.hcmute.shopfee.entity.sql.database.AddressEntity;

import java.util.List;

public interface IAddressService {
    AddressEntity createAddressToUser(CreateAddressRequest body, String userId);
    void updateAddressById(UpdateAddressRequest body, String addressId);
    void deleteAddressById(String addressId);
    List<GetAddressListByUserIdResponse> getAddressListByUserId(String userId);
    GetAddressDetailsByIdResponse getAddressDetailById(String addressId);
}

