package com.cskaoyan.mall.product.mapper;

import com.cskaoyan.mall.product.model.CategoryHierarchy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryHierarchyMapper {
    /*
          1. 当thirdLevelCategoryId不为null，即查询某一个三级类目的一二级类目
             结果集中只会有一个CategoryHierarchy对象
          2. 当thirdLevelCategoryId为null，即查询所有三级类目的一二级类目
             结果集中包含多个CategoryHierarchy对象
     */
    List<CategoryHierarchy> selectCategoryHierarchy(@Param("thirdLevelCategoryId") Long thirdLevelCategoryId);
}
