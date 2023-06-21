package com.cskaoyan.mall.user.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

@Data
//用户地址
@TableName("user_address")
public class UserAddress extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // "用户地址"
    @TableField("user_address")
    private String userAddress;

    //"用户id"
    @TableField("user_id")
    private Long userId;

    // "收件人"
    @TableField("consignee")
    private String consignee;

    // "联系方式"
    @TableField("phone_num")
    private String phoneNum;

    // "是否是默认"
    @TableField("is_default")
    private String isDefault;

}

