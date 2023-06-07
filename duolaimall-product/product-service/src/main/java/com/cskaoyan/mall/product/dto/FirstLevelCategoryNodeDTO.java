package com.cskaoyan.mall.product.dto;


import lombok.Data;

import java.util.List;

@Data
public class FirstLevelCategoryNodeDTO {


    Long categoryId;


    String categoryName;

    //"一级目录的位序，给遍历顺序即可"
    Integer index;

    //"一级目录所包含的二级目录"
    List<SecondLevelCategoryNodeDTO> categoryChild;

}
