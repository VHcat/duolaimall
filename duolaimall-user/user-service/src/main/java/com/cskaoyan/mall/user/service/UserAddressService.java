package com.cskaoyan.mall.user.service;


import com.cskaoyan.mall.user.model.UserAddress;

import java.util.List;

public interface UserAddressService {

    /**
     * 根据用户Id 查询用户的收货地址列表！
     * @param userId
     * @return
     */
    List<UserAddress> findUserAddressListByUserId(String userId);
}
