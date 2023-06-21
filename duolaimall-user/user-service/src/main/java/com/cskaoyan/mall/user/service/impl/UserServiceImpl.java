package com.cskaoyan.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cskaoyan.mall.common.constant.RedisConst;
import com.cskaoyan.mall.user.consts.UserConstants;
import com.cskaoyan.mall.user.dto.UserLoginDTO;
import com.cskaoyan.mall.user.dto.UserLoginInfoDTO;
import com.cskaoyan.mall.user.mapper.UserInfoMapper;
import com.cskaoyan.mall.user.model.UserInfo;
import com.cskaoyan.mall.user.query.UserInfoParam;
import com.cskaoyan.mall.user.service.UserService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings("all")
public class UserServiceImpl implements UserService {

    // 调用mapper 层
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedissonClient redissonClient;


    @Override
    public UserLoginDTO login(UserInfoParam userInfo, String ip, String token) {
        String passwd = userInfo.getPasswd();
        // 获取密文密码
        String newPasswd = DigestUtils.md5DigestAsHex(passwd.getBytes());

        // 匹配用户的用户名和密码
        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userInfoLambdaQueryWrapper.eq(UserInfo::getLoginName, userInfo.getLoginName());
        userInfoLambdaQueryWrapper.eq(UserInfo::getPasswd, newPasswd);

        UserInfo user = userInfoMapper.selectOne(userInfoLambdaQueryWrapper);
        if (user == null) {
           // 登录失败
           return null;
        }

        // 用户可以登录成功，将用户的信息保存到Redis中
        // 封装了用户的userId 以及登录成功时的ip地址
        UserLoginInfoDTO userLoginInfoDTO = new UserLoginInfoDTO();
        userLoginInfoDTO.setUserId(user.getId().toString());
        userLoginInfoDTO.setIp(ip);

        RBucket<UserLoginInfoDTO> bucket = redissonClient.getBucket(UserConstants.USER_LOGIN_KEY_PREFIX + token);
        // 保存并设置过期时间
        bucket.set(userLoginInfoDTO, RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);

        return new UserLoginDTO(user.getNickName(), token);
    }
}
