package com.cskaoyan.mall.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAddressDTO implements Serializable {

    Long id;


    private String userAddress;


    private Long userId;


    private String consignee;


    private String phoneNum;


    private String isDefault;
}
