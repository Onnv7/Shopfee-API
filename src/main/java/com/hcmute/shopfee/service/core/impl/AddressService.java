package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.payload.request.CreateAddressRequest;
import com.hcmute.shopfee.payload.request.UpdateAddressRequest;
import com.hcmute.shopfee.payload.response.GetAddressDetailsByIdResponse;
import com.hcmute.shopfee.payload.response.GetAddressListByUserIdResponse;
import com.hcmute.shopfee.entity.sql.database.user.AddressEntity;
import com.hcmute.shopfee.entity.sql.database.user.UserEntity;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.AddressRepository;
import com.hcmute.shopfee.repository.database.UserRepository;
import com.hcmute.shopfee.service.core.IAddressService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hcmute.shopfee.constant.ErrorConstant.*;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapperService modelMapperService;

    @Override
    public AddressEntity createAddressToUser(CreateAddressRequest body, String userId) {
        SecurityUtils.checkUserId(userId);
        AddressEntity data = modelMapperService.mapClass(body, AddressEntity.class);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.USER_NOT_FOUND, NOT_FOUND_WITH_INPUT + userId));
        data.setUser(user);

        if (user.getAddressList().size() >= 5) {
            throw new ShopfeeException(ShopfeeErrorCode.ADD_MORE_5_ADDRESS);
        }
        data.setDefault(user.getAddressList().isEmpty());
        return addressRepository.save(data);
    }

    @Override
    public void updateAddressById(UpdateAddressRequest body, String addressId) {
        AddressEntity data = modelMapperService.mapClass(body, AddressEntity.class);
        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ADDRESS_NOT_FOUND, NOT_FOUND_WITH_INPUT + addressId));
        UserEntity user = address.getUser();
        SecurityUtils.checkUserId(user.getId());
        if (data.isDefault()) {
            List<AddressEntity> addressList = user.getAddressList();
            for (AddressEntity add : addressList) {
                add.setDefault(false);
                addressRepository.save(add);
            }
            address.setDefault(true);
            addressRepository.save(address);
        }
        modelMapperService.mapNotNull(data, address);
        addressRepository.save(address);
    }

    @Override
    public void deleteAddressById(String addressId) {
        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ADDRESS_NOT_FOUND, NOT_FOUND_WITH_INPUT + addressId));

        UserEntity user = address.getUser();
        SecurityUtils.checkUserId(user.getId());
        addressRepository.deleteById(addressId);
    }

    @Override
    public List<GetAddressListByUserIdResponse> getAddressListByUserId(String userId) {
        SecurityUtils.checkUserId(userId);

        List<GetAddressListByUserIdResponse> list = GetAddressListByUserIdResponse.fromAddressEntityList(
                addressRepository.findByUserIdOrderByIsDefaultDesc(userId)
        );
        return list;
    }

    @Override
    public GetAddressDetailsByIdResponse getAddressDetailById(String addressId) {
        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ADDRESS_NOT_FOUND, NOT_FOUND_WITH_INPUT + addressId));
        SecurityUtils.checkUserId(address.getUser().getId());
        return modelMapperService.mapClass(address, GetAddressDetailsByIdResponse.class);
    }

}
