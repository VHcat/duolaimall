package com.cskaoyan.mall.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class SecondLevelCategoryNodeDTO {

    //"二级目录id"
    Long categoryId;

    //"二级目录名称"
    String categoryName;

    //"二级目录所包含的三级目录"
    List<ThirdLevelCategoryNodeDTO> categoryChild;
}
