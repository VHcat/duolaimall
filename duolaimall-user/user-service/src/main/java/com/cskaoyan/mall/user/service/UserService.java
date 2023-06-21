package com.cskaoyan.mall.user.service;


import com.cskaoyan.mall.user.dto.UserLoginDTO;
import com.cskaoyan.mall.user.query.UserInfoParam;

public interface UserService {

    /**
     * 登录方法
     */
    UserLoginDTO login(UserInfoParam userInfo, String ip, String token);

}
