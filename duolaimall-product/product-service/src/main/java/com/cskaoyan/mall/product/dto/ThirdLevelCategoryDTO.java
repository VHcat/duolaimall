package com.cskaoyan.mall.product.dto;

import lombok.Data;

@Data
public class ThirdLevelCategoryDTO {

    //"三级分类id"
    private Long id;

    //"三级分类名称"
    private String name;

    //"二级分类编号"
    private Long secondLevelCategoryId;
}
