package com.cskaoyan.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cskaoyan.mall.common.cache.RedisCache;
import com.cskaoyan.mall.product.converter.dto.CategoryConverter;
import com.cskaoyan.mall.product.converter.dto.TrademarkConverter;
import com.cskaoyan.mall.product.dto.*;
import com.cskaoyan.mall.product.mapper.*;
import com.cskaoyan.mall.product.model.*;
import com.cskaoyan.mall.product.query.CategoryTrademarkParam;
import com.cskaoyan.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    FirstLevelCategoryMapper firstLevelCategoryMapper;

    @Autowired
    SecondLevelCategoryMapper secondLevelCategoryMapper;

    @Autowired
    ThirdLevelCategoryMapper thirdLevelCategoryMapper;

    @Autowired
    CategoryTrademarkMapper categoryTrademarkMapper;

    @Autowired
    CategoryHierarchyMapper categoryHierarchyMapper;

    @Autowired
    TrademarkMapper trademarkMapper;

    @Autowired
    CategoryConverter categoryConverter;
    @Autowired
    TrademarkConverter trademarkConverter;


    @Override
    public List<FirstLevelCategoryDTO> getFirstLevelCategory() {
        // 查询所有的一级类目
        List<FirstLevelCategory> firstLevelCategories = firstLevelCategoryMapper.selectList(null);
        List<FirstLevelCategoryDTO> firstLevelCategoryDTOs = categoryConverter.firstLevelCategoryPOs2DTOs(firstLevelCategories);
        return firstLevelCategoryDTOs;
    }

    @Override
    public List<SecondLevelCategoryDTO> getSecondLevelCategory(Long firstLevelCategoryId) {
        LambdaQueryWrapper<SecondLevelCategory> secondLevelCategoryQueryWrapper = new LambdaQueryWrapper<>();
        secondLevelCategoryQueryWrapper.eq(SecondLevelCategory::getFirstLevelCategoryId, firstLevelCategoryId);
        // 根据一级类目查询所属的二级类目
        List<SecondLevelCategory> secondLevelCategories = secondLevelCategoryMapper.selectList(secondLevelCategoryQueryWrapper);
        // PO -> DTO
        List<SecondLevelCategoryDTO> secondLevelCategoryDTOs = categoryConverter.secondLevelCategoryPOs2DTOs(secondLevelCategories);
        return secondLevelCategoryDTOs;
    }

    @Override
    public List<ThirdLevelCategoryDTO> getThirdLevelCategory(Long secondLevelCategoryId) {
        // 查询二级类目所属的三级类目
        LambdaQueryWrapper<ThirdLevelCategory> thirdLevelCategoryQueryWrapper = new LambdaQueryWrapper<>();
        thirdLevelCategoryQueryWrapper.eq(ThirdLevelCategory::getSecondLevelCategoryId, secondLevelCategoryId);

        List<ThirdLevelCategory> thirdLevelCategories = thirdLevelCategoryMapper.selectList(thirdLevelCategoryQueryWrapper);

        // PO -> DTO
        List<ThirdLevelCategoryDTO> thirdLevelCategoryDTOS = categoryConverter.thirdLevelCategoryPOs2DTOs(thirdLevelCategories);
        return thirdLevelCategoryDTOS;
    }

    @Override
    public List<TrademarkDTO> findTrademarkList(Long category3Id) {
        // 在category_trademark表中根据类目，查询所属所有商品id
        LambdaQueryWrapper<CategoryTrademark> categoryTrademarkQueryWrapper = new LambdaQueryWrapper<>();
        categoryTrademarkQueryWrapper.eq(CategoryTrademark::getThirdLevelCategoryId, category3Id);

        List<CategoryTrademark> categoryTrademarks = categoryTrademarkMapper.selectList(categoryTrademarkQueryWrapper);

        //  判断CategoryTrademarkList 这个集合
        if(!CollectionUtils.isEmpty(categoryTrademarks)){
            //  需要获取到这个集合中的品牌Id 集合数据
            List<Long> tradeMarkIdList = categoryTrademarks.stream().map(baseCategoryTrademark -> {
                return baseCategoryTrademark.getTrademarkId();
            }).collect(Collectors.toList());
            //  正常查询数据的话... 需要根据品牌Id 来获取集合数据！
            List<Trademark> trademarks = trademarkMapper.selectBatchIds(tradeMarkIdList);
            List<TrademarkDTO> trademarkDTOs = trademarkConverter.trademarkPOs2DTOs(trademarks);
            return trademarkDTOs;

        }
        //  如果集合为空，则默认返回空

        return null;
    }

    @Override
    public void save(CategoryTrademarkParam categoryTrademarkParam) {
        //  获取到品牌Id 集合数据
        List<Long> trademarkIdList = categoryTrademarkParam.getTrademarkIdList();

        //  判断
        if (!CollectionUtils.isEmpty(trademarkIdList)){
            //  做映射关系
          trademarkIdList.stream().map((trademarkId) -> {
                //  创建一个分类Id 与品牌的关联的对象
                CategoryTrademark categoryTrademark = new CategoryTrademark();
                categoryTrademark.setThirdLevelCategoryId(categoryTrademarkParam.getCategory3Id());
                categoryTrademark.setTrademarkId(trademarkId);
                //  返回数据
                return categoryTrademark;
            }).forEach(
                    categoryTrademark -> categoryTrademarkMapper.insert(categoryTrademark)
          );
        }
    }

    @Override
    public List<TrademarkDTO> findUnLinkedTrademarkList(Long category3Id) {
        //  哪些是关联的品牌Id
        LambdaQueryWrapper<CategoryTrademark> baseCategoryTrademarkQueryWrapper = new LambdaQueryWrapper<>();
        baseCategoryTrademarkQueryWrapper.eq(CategoryTrademark::getThirdLevelCategoryId,category3Id);
        List<CategoryTrademark> baseCategoryTrademarkList
                = categoryTrademarkMapper.selectList(baseCategoryTrademarkQueryWrapper);

        //  判断
        if (!CollectionUtils.isEmpty(baseCategoryTrademarkList)){
            //  找到关联的品牌Id 集合数据 {1,3}
            List<Long> tradeMarkIdList = baseCategoryTrademarkList.stream().map(categoryTrademark -> {
                return categoryTrademark.getTrademarkId();
            }).collect(Collectors.toList());
            //  在所有的品牌Id 中将这些有关联的品牌Id 给过滤掉就可以！
            //  select * from base_trademark; 外面 baseTrademarkMapper.selectList(null) {1,2,3,5}
            List<Trademark> trademarkList = trademarkMapper.selectList(null).stream()
                    .filter(baseTrademark -> !tradeMarkIdList.contains(baseTrademark.getId()))
                    .collect(Collectors.toList());

            List<TrademarkDTO> trademarkDTOs = trademarkConverter.trademarkPOs2DTOs(trademarkList);
            //  返回数据
            return trademarkDTOs;
        }
        //  如果说这个三级分类Id 下 没有任何品牌！ 则获取到所有的品牌数据！
        List<Trademark> trademarks = trademarkMapper.selectList(null);
        return trademarkConverter.trademarkPOs2DTOs(trademarks);
    }

    @Override
    public void remove(Long category3Id, Long trademarkId) {
        //  逻辑删除： 本质更新操作 is_deleted
        //  更新： update base_category_trademark set is_deleted = 1 where category3_id=? and trademark_id=?;
        LambdaQueryWrapper<CategoryTrademark> categoryTrademarkQueryWrapper = new LambdaQueryWrapper<>();
        categoryTrademarkQueryWrapper.eq(CategoryTrademark::getThirdLevelCategoryId,category3Id);
        categoryTrademarkQueryWrapper.eq(CategoryTrademark::getTrademarkId,trademarkId);
        categoryTrademarkMapper.delete(categoryTrademarkQueryWrapper);
    }

    @Override
    @RedisCache(prefix = "categoryHierarchyByCategory3Id:")
    public CategoryHierarchyDTO getCategoryViewByCategoryId(Long category3Id) {
        List<CategoryHierarchy> categoryHierarchies = categoryHierarchyMapper.selectCategoryHierarchy(category3Id);
        if (CollectionUtils.isEmpty(categoryHierarchies)) {
            return null;
        }
        return categoryConverter.categoryViewPO2DTO(categoryHierarchies.get(0));
    }

    @Override
    //@RedisCache(prefix = "category")
    public List<FirstLevelCategoryNodeDTO> getCategoryTreeList() {
        // 声明几个json 集合
        ArrayList<FirstLevelCategoryNodeDTO> firstLevelCategoryTreeNodes = new ArrayList<>();
        // 声明获取所有分类数据集合
        List<CategoryHierarchy> categoryHierarchyList = categoryHierarchyMapper.selectCategoryHierarchy(null);
        // 循环上面的集合并安一级分类Id 进行分组
        Map<Long, List<CategoryHierarchy>> firstLevelCategoryMap  = categoryHierarchyList.stream().collect(Collectors.groupingBy(CategoryHierarchy::getFirstLevelCategoryId));
        int index = 1;
        // 获取一级分类下所有数据
        for (Map.Entry<Long, List<CategoryHierarchy>> firstLevelEntry  : firstLevelCategoryMap.entrySet()) {
            // 获取一级分类Id
            Long firstLevelCategoryId  = firstLevelEntry.getKey();
            // 获取一级分类下面的所有集合
            List<CategoryHierarchy> firstLevelCategories  = firstLevelEntry.getValue();
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
        Map<Long, List<CategoryHierarchy>> firstLevelCategoryChildrenMap  = firstLevelCategories.stream()
                .collect(Collectors.groupingBy(CategoryHierarchy::getSecondLevelCategoryId));

        // 循环遍历
        for (Map.Entry<Long, List<CategoryHierarchy>> secondLevelEntry  : firstLevelCategoryChildrenMap.entrySet()) {
            // 获取二级分类Id
            Long secondLevelCategoryId  = secondLevelEntry.getKey();
            // 获取二级分类下的所有集合
            List<CategoryHierarchy> secondLevelCategories  = secondLevelEntry.getValue();
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
        List<ThirdLevelCategoryNodeDTO> thirdLevelCategoryNodeDTOs = secondLevelCategories.stream().map(categoryHierarchy -> {
            ThirdLevelCategoryNodeDTO thirdLevelCategoryNode = new ThirdLevelCategoryNodeDTO();
            thirdLevelCategoryNode.setCategoryId(categoryHierarchy.getThirdLevelCategoryId());
            thirdLevelCategoryNode.setCategoryName(categoryHierarchy.getThirdLevelCategoryName());
            return thirdLevelCategoryNode;
        }).collect(Collectors.toList());

        return thirdLevelCategoryNodeDTOs;
    }

}
