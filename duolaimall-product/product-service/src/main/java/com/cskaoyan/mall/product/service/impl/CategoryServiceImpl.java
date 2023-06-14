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
import org.springframework.util.CollectionUtils;

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
    @Resource
    CategoryHierarchyMapper categoryHierarchyMapper;

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
        List<CategoryHierarchy> categoryHierarchies = categoryHierarchyMapper.selectCategoryHierarchy(thirdLevelCategoryId);
        if (CollectionUtils.isEmpty(categoryHierarchies)) {
            return null;
        }
        return categoryConverter.categoryViewPO2DTO(categoryHierarchies.get(0));
    }

    @Override
    public List<FirstLevelCategoryNodeDTO> getCategoryTreeList() {
        // 声明几个json 集合
        ArrayList<FirstLevelCategoryNodeDTO> firstLevelCategoryTreeNodes = new ArrayList<>();
        // 声明获取所有分类数据集合
        List<CategoryHierarchy> categoryHierarchyList = categoryHierarchyMapper.selectCategoryHierarchy(null);
        // 循环上面的集合并安一级分类Id 进行分组
        Map<Long, List<CategoryHierarchy>> firstLevelCategoryMap = categoryHierarchyList.stream()
                .collect(Collectors.groupingBy(CategoryHierarchy::getFirstLevelCategoryId));
        int index = 1;
        // 获取一级分类下所有数据
        for (Map.Entry<Long, List<CategoryHierarchy>> firstLevelEntry : firstLevelCategoryMap.entrySet()) {
            // 获取一级分类Id
            Long firstLevelCategoryId = firstLevelEntry.getKey();
            // 获取一级分类下面的所有集合
            List<CategoryHierarchy> firstLevelCategories = firstLevelEntry.getValue();
            //
            FirstLevelCategoryNodeDTO firstLevelCategoryNode = new FirstLevelCategoryNodeDTO();
            firstLevelCategoryNode.setIndex(index);
            firstLevelCategoryNode.setCategoryId(firstLevelCategoryId);
            firstLevelCategoryNode.setCategoryName(firstLevelCategories.get(0).getFirstLevelCategoryName());
            // 变量迭代
            index++;
            List<SecondLevelCategoryNodeDTO> secondLevelCategoryNodes = buildSecondLevelCategoryNodeDTOs(firstLevelCategories);

            // 将二级数据放入一级里面
            firstLevelCategoryNode.setCategoryChild(secondLevelCategoryNodes);
            // 将一级类目放入最终结果集
            firstLevelCategoryTreeNodes.add(firstLevelCategoryNode);
        }
        return firstLevelCategoryTreeNodes;
    }

    private List<SecondLevelCategoryNodeDTO> buildSecondLevelCategoryNodeDTOs(List<CategoryHierarchy> firstLevelCategories) {

        // 声明二级分类对象集合
        List<SecondLevelCategoryNodeDTO> secondLevelCategoryNodes = new ArrayList<>();

        // 循环获取二级分类数据
        Map<Long, List<CategoryHierarchy>> firstLevelCategoryChildrenMap = firstLevelCategories.stream()
                .collect(Collectors.groupingBy(CategoryHierarchy::getSecondLevelCategoryId));

        // 循环遍历
        for (Map.Entry<Long, List<CategoryHierarchy>> secondLevelEntry : firstLevelCategoryChildrenMap.entrySet()) {
            // 获取二级分类Id
            Long secondLevelCategoryId = secondLevelEntry.getKey();
            // 获取二级分类下的所有集合
            List<CategoryHierarchy> secondLevelCategories = secondLevelEntry.getValue();
            // 声明二级分类对象
            SecondLevelCategoryNodeDTO secondLevelCategoryNode = new SecondLevelCategoryNodeDTO();
            secondLevelCategoryNode.setCategoryId(secondLevelCategoryId);
            secondLevelCategoryNode.setCategoryName(secondLevelCategories.get(0).getSecondLevelCategoryName());

            List<ThirdLevelCategoryNodeDTO> thirdLevelCategoryNodes = buildThirdLevelCategoryNodes(secondLevelCategories);
            // 将三级类目列表放入二级类目中
            secondLevelCategoryNode.setCategoryChild(thirdLevelCategoryNodes);

            // 添加到二级分类集合
            secondLevelCategoryNodes.add(secondLevelCategoryNode);
        }
        return secondLevelCategoryNodes;
    }

    private List<ThirdLevelCategoryNodeDTO> buildThirdLevelCategoryNodes(List<CategoryHierarchy> secondLevelCategories) {
        // 循环三级分类数据, 封装为ThirdLevelCategoryNodeDTO
        List<ThirdLevelCategoryNodeDTO> thirdLevelCategoryNodeDTOs = secondLevelCategories.stream()
                .map(categoryHierarchy -> {
                    ThirdLevelCategoryNodeDTO thirdLevelCategoryNode = new ThirdLevelCategoryNodeDTO();
                    thirdLevelCategoryNode.setCategoryId(categoryHierarchy.getThirdLevelCategoryId());
                    thirdLevelCategoryNode.setCategoryName(categoryHierarchy.getThirdLevelCategoryName());
                    return thirdLevelCategoryNode;
                })
                .collect(Collectors.toList());

        return thirdLevelCategoryNodeDTOs;
    }
}
