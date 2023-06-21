package com.cskaoyan.mall.user.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

@Data
// userInfo
@TableName("user_info")
public class UserInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // 用户名称
    @TableField("login_name")
    private String loginName;

    // 用户昵称
    @TableField("nick_name")
    private String nickName;

    // 用户密码
    @TableField("passwd")
    private String passwd;

    //用户姓名
    @TableField("name")
    private String name;

    // "手机号"
    @TableField("phone_num")
    private String phoneNum;

    // "邮箱"
    @TableField("email")
    private String email;

    // "头像"
    @TableField("head_img")
    private String headImg;

    // "用户级别"
    @TableField("user_level")
    private String userLevel;

}
