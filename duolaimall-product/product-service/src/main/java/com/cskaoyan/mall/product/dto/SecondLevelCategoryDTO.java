package com.cskaoyan.mall.product.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SecondLevelCategoryDTO {

    //"二级分类id"
    private Long id;

    //"二级分类名称"
    private String name;

    //"一级分类编号"
    private Long firstLevelCategoryId;
}
