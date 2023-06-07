package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

@Data
@TableName("second_level_category")
public class SecondLevelCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //"二级分类名称"
    @TableField("name")
    private String name;

    //"一级分类编号"
    @TableField("first_level_category_id")
    private Long firstLevelCategoryId;

}
