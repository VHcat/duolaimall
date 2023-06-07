package com.cskaoyan.mall.product.converter.dto;

import com.cskaoyan.mall.product.dto.CategoryHierarchyDTO;
import com.cskaoyan.mall.product.dto.FirstLevelCategoryDTO;
import com.cskaoyan.mall.product.dto.SecondLevelCategoryDTO;
import com.cskaoyan.mall.product.dto.ThirdLevelCategoryDTO;
import com.cskaoyan.mall.product.model.CategoryHierarchy;
import com.cskaoyan.mall.product.model.FirstLevelCategory;
import com.cskaoyan.mall.product.model.SecondLevelCategory;
import com.cskaoyan.mall.product.model.ThirdLevelCategory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryConverter {

    FirstLevelCategoryDTO firstLevelCategoryPO2DTO(FirstLevelCategory firstLevelCategory);
    List<FirstLevelCategoryDTO> firstLevelCategoryPOs2DTOs(List<FirstLevelCategory> firstLevelCategories);

    SecondLevelCategoryDTO secondLevelCategoryPO2DTO(SecondLevelCategory secondLevelCategory);
    List<SecondLevelCategoryDTO> secondLevelCategoryPOs2DTOs(List<SecondLevelCategory> secondLevelCategories);

    ThirdLevelCategoryDTO thirdLevelCategoryPO2DTO(ThirdLevelCategory thirdLevelCategory);
    List<ThirdLevelCategoryDTO> thirdLevelCategoryPOs2DTOs(List<ThirdLevelCategory> thirdLevelCategories);

    CategoryHierarchyDTO categoryViewPO2DTO(CategoryHierarchy categoryHierarchy);

}
