package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

@Data
@TableName("first_level_category")
public class FirstLevelCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //"分类名称"
    @TableField("name")
    private String name;

}
