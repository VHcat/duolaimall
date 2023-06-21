package com.cskaoyan.mall.user.dto;

import lombok.Data;

/*
      用户登录成功后，存储在Redis中的用户信息
 */
@Data
public class UserLoginInfoDTO {

    String userId;

    String ip;
}
