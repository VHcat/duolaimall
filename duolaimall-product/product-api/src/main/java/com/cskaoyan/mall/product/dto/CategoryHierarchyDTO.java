package com.cskaoyan.mall.product.dto;

import lombok.Data;

@Data
public class CategoryHierarchyDTO {

    private Long id;

    //"一级分类编号"
    private Long firstLevelCategoryId;

    //"一级分类名称"
    private String firstLevelCategoryName;

    //"二级分类编号"
    private Long secondLevelCategoryId;

    //"二级分类名称"
    private String secondLevelCategoryName;

    //"三级分类编号"
    private Long thirdLevelCategoryId;

    //"三级分类名称"
    private String thirdLevelCategoryName;
}
