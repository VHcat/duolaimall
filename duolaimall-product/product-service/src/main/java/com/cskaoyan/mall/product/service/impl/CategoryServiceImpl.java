package com.cskaoyan.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cskaoyan.mall.product.converter.dto.CategoryConverter;
import com.cskaoyan.mall.product.converter.dto.TrademarkConverter;
import com.cskaoyan.mall.product.dto.*;
import com.cskaoyan.mall.product.mapper.*;
import com.cskaoyan.mall.product.model.*;
import com.cskaoyan.mall.product.query.CategoryTrademarkParam;
import com.cskaoyan.mall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Resource
    TrademarkMapper trademarkMapper;
    @Resource
    TrademarkConverter trademarkConverter;
    @Resource
    CategoryTrademarkMapper categoryTrademarkMapper;

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
        // 先通过三级类目类目查询品牌id，再通过品牌id查询其余数据
//        QueryWrapper<CategoryTrademark> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("third_level_category_id", category3Id);
//        List<CategoryTrademark> categoryTrademarkList = categoryTrademarkMapper.selectList(queryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("third_level_category_id", category3Id);
        List<CategoryTrademark> categoryTrademarkList = categoryTrademarkMapper.selectByMap(map);
        // 获得品牌id
        List<Trademark> trademarks = new ArrayList<>();
        for (CategoryTrademark categoryTrademark : categoryTrademarkList) {
            Long trademarkId = categoryTrademark.getTrademarkId();
            Trademark trademark = trademarkMapper.selectById(trademarkId);
            trademarks.add(trademark);
        }
        return trademarkConverter.trademarkPOs2DTOs(trademarks);
    }


    @Override
    public void save(CategoryTrademarkParam categoryTrademarkParam) {
        List<Long> trademarkIdList = categoryTrademarkParam.getTrademarkIdList();
        Long category3Id = categoryTrademarkParam.getCategory3Id();
        for (Long aLong : trademarkIdList) {
            CategoryTrademark categoryTrademark = new CategoryTrademark();
            categoryTrademark.setThirdLevelCategoryId(category3Id);
            categoryTrademark.setTrademarkId(aLong);
            // 插入
            categoryTrademarkMapper.insert(categoryTrademark);
        }

    }

    @Override
    public List<TrademarkDTO> findUnLinkedTrademarkList(Long thirdLevelCategoryId) {
        // 查询已经分类的品牌id
        List<CategoryTrademark> categoryTrademarkList = categoryTrademarkMapper.selectList(null);
        List<Long> categoryTrademarkIds = new ArrayList<>();
        for (CategoryTrademark categoryTrademark : categoryTrademarkList) {
            Long categoryTrademarkId = categoryTrademark.getTrademarkId();
            categoryTrademarkIds.add(categoryTrademarkId);
        }
        // 总品牌id
        List<Trademark> trademarkList = trademarkMapper.selectList(null);
        List<Long> trademarkIds = new ArrayList<>();
        for (Trademark trademark : trademarkList) {
            Long id = trademark.getId();
            trademarkIds.add(id);
        }
        // 所有的品牌类目去除已经被分类的品牌类目
        // 对于category_trademark表中全部id和当前id取差集
        List<Long> difference = trademarkIds.stream()
                .filter(item -> !categoryTrademarkIds.contains(item))
                .collect(Collectors.toList());
        List<Trademark> unTrademarkList = trademarkMapper.selectBatchIds(difference);
        return trademarkConverter.trademarkPOs2DTOs(unTrademarkList);
    }

    @Override
    public void remove(Long thirdLevelCategoryId, Long trademarkId) {
        // 构建wrapper
        QueryWrapper<CategoryTrademark> wrapper = new QueryWrapper<>();
        wrapper.eq("third_level_category_id", thirdLevelCategoryId);
        wrapper.eq("trademark_id", trademarkId);
        // 删除
        categoryTrademarkMapper.delete(wrapper);
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
