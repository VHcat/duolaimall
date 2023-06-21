package com.cskaoyan.mall.user.converter;


import com.cskaoyan.mall.user.dto.UserAddressDTO;
import com.cskaoyan.mall.user.model.UserAddress;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAddressConverter {

    UserAddressDTO userAddressPO2DTO(UserAddress userAddress);
    List<UserAddressDTO> userAddressPOs2DTOs(List<UserAddress> userAddresses);
}
