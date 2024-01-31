package com.hcmute.shopfee.service.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.CreateAddressRequest;
import com.hcmute.shopfee.dto.request.UpdateAddressRequest;
import com.hcmute.shopfee.dto.response.GetAddressDetailsByIdResponse;
import com.hcmute.shopfee.dto.response.GetAddressListByUserIdResponse;
import com.hcmute.shopfee.entity.AddressEntity;
import com.hcmute.shopfee.entity.UserEntity;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.AddressRepository;
import com.hcmute.shopfee.repository.database.UserRepository;
import com.hcmute.shopfee.service.IAddressService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_FOUND + userId));
        data.setUser(user);

        if (user.getAddressList().size() >= 5) {
            throw new CustomException(ErrorConstant.OVER_FIVE_ADDRESS);
        }
        data.setDefault(user.getAddressList().isEmpty());
        return addressRepository.save(data);
    }

    @Override
    public void updateAddressById(UpdateAddressRequest body, String addressId) {
        AddressEntity data = modelMapperService.mapClass(body, AddressEntity.class);
        AddressEntity address = addressRepository.findById(addressId).orElseThrow(() -> new CustomException(NOT_FOUND + addressId));
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
        AddressEntity address = addressRepository.findById(addressId).orElseThrow(() -> new CustomException(NOT_FOUND + addressId));

        UserEntity user = address.getUser();
        SecurityUtils.checkUserId(user.getId());
        addressRepository.deleteById(addressId);
    }

    @Override
    public List<GetAddressListByUserIdResponse> getAddressListByUserId(String userId) {
        SecurityUtils.checkUserId(userId);

        List<GetAddressListByUserIdResponse> list = modelMapperService.mapList(addressRepository.findByUser_Id(userId), GetAddressListByUserIdResponse.class);
       // TODO: xem chỗ sort làm gì -> có thể chuyển sang sql query
        Collections.sort(list, new Comparator<GetAddressListByUserIdResponse>() {
            @Override
            public int compare(GetAddressListByUserIdResponse address1, GetAddressListByUserIdResponse address2) {
                if (address1.isDefault() && !address2.isDefault()) {
                    return -1;
                } else if (!address1.isDefault() && address2.isDefault()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return list;
    }

    @Override
    public GetAddressDetailsByIdResponse getAddressDetailById(String addressId) {
        AddressEntity address = addressRepository.findById(addressId).orElseThrow(() -> new CustomException(NOT_FOUND + addressId));
        SecurityUtils.checkUserId(address.getUser().getId());
        return modelMapperService.mapClass(address, GetAddressDetailsByIdResponse.class);
    }

}
