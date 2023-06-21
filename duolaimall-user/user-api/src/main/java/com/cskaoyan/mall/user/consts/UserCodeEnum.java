package com.cskaoyan.mall.user.consts;


import com.cskaoyan.mall.common.constant.CodeEnum;
import lombok.Getter;

@Getter
public enum UserCodeEnum implements CodeEnum {

    USER_LOGIN_CHECK_FAIL(1001, "用户名或密码错误");


    UserCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;
}
