package com.cskaoyan.mall.user.controller;

import com.cskaoyan.mall.user.dto.UserAddressDTO;
import com.cskaoyan.mall.user.model.UserAddress;
import com.cskaoyan.mall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 北海 on 2023-05-04 17:15
 */
@RestController
public class UserApiController {

    @Autowired
    UserAddressService userAddressService;

    @GetMapping("/api/user/inner/findUserAddressListByUserId/{userId}")
    public List<UserAddressDTO> findUserAddressListByUserId(@PathVariable("userId") String userId){
        List<UserAddress> userAddressListByUserId = userAddressService.findUserAddressListByUserId(userId);
        return null;
    }
}
