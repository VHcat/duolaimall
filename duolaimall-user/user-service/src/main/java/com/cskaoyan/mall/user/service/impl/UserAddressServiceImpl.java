package com.cskaoyan.mall.user.service.impl;

import com.cskaoyan.mall.user.converter.UserAddressConverter;
import com.cskaoyan.mall.user.model.UserAddress;
import com.cskaoyan.mall.user.service.UserAddressService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 北海 on 2023-05-04 17:16
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Override
    public List<UserAddress> findUserAddressListByUserId(String userId) {
        return null;
    }
}
