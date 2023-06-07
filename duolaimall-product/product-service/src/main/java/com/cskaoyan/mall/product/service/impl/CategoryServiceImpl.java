package com.cskaoyan.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cskaoyan.mall.product.converter.dto.CategoryConverter;
import com.cskaoyan.mall.product.dto.*;
import com.cskaoyan.mall.product.mapper.FirstLevelCategoryMapper;
import com.cskaoyan.mall.product.mapper.SecondLevelCategoryMapper;
import com.cskaoyan.mall.product.mapper.ThirdLevelCategoryMapper;
import com.cskaoyan.mall.product.model.FirstLevelCategory;
import com.cskaoyan.mall.product.model.SecondLevelCategory;
import com.cskaoyan.mall.product.model.ThirdLevelCategory;
import com.cskaoyan.mall.product.query.CategoryTrademarkParam;
import com.cskaoyan.mall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/07 15:58
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    FirstLevelCategoryMapper firstLevelCategoryMapper;
    @Resource
    CategoryConverter categoryConverter;

    @Override
    public List<FirstLevelCategoryDTO> getFirstLevelCategory() {
        List<FirstLevelCategory> firstLevelCategories = firstLevelCategoryMapper.selectList(null);
        return categoryConverter.firstLevelCategoryPOs2DTOs(firstLevelCategories);
    }

    @Resource
    SecondLevelCategoryMapper secondLevelCategoryMapper;


    @Override
    public List<SecondLevelCategoryDTO> getSecondLevelCategory(Long firstLevelCategoryId) {
        // 构造查询条件
        QueryWrapper<SecondLevelCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("first_level_category_id", firstLevelCategoryId);
        List<SecondLevelCategory> secondLevelCategories = secondLevelCategoryMapper.selectList(queryWrapper);
        return categoryConverter.secondLevelCategoryPOs2DTOs(secondLevelCategories);
    }

    @Resource
    ThirdLevelCategoryMapper thirdLevelCategoryMapper;

    @Override
    public List<ThirdLevelCategoryDTO> getThirdLevelCategory(Long secondLevelCategoryId) {
        // 构造查询条件
        QueryWrapper<ThirdLevelCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("second_level_category_id", secondLevelCategoryId);
        List<ThirdLevelCategory> thirdLevelCategories = thirdLevelCategoryMapper.selectList(queryWrapper);
        return categoryConverter.thirdLevelCategoryPOs2DTOs(thirdLevelCategories);
    }

    @Override
    public List<TrademarkDTO> findTrademarkList(Long category3Id) {
        return null;
    }

    @Override
    public void save(CategoryTrademarkParam categoryTrademarkParam) {

    }

    @Override
    public List<TrademarkDTO> findUnLinkedTrademarkList(Long thirdLevelCategoryId) {
        return null;
    }

    @Override
    public void remove(Long thirdLevelCategoryId, Long trademarkId) {

    }

    @Override
    public CategoryHierarchyDTO getCategoryViewByCategoryId(Long thirdLevelCategoryId) {
        return null;
    }

    @Override
    public List<FirstLevelCategoryNodeDTO> getCategoryTreeList() {
        return null;
    }




}
