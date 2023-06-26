package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.*;
import com.cskaoyan.mall.product.query.CategoryTrademarkParam;
import com.cskaoyan.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("admin/product")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @PostMapping("/baseCategoryTrademark/save")
    public Result save(@RequestBody CategoryTrademarkParam categoryTrademarkVo){
        //  保存方法
        categoryService.save(categoryTrademarkVo);
        return Result.ok();
    }

    @DeleteMapping("/baseCategoryTrademark/remove/{thirdLevelCategoryId}/{trademarkId}")
    public Result remove(@PathVariable Long thirdLevelCategoryId, @PathVariable Long trademarkId){
        //  调用服务层方法
        categoryService.remove(thirdLevelCategoryId, trademarkId);
        return Result.ok();
    }

    @GetMapping("/baseCategoryTrademark/findTrademarkList/{thirdLevelCategoryId}")
    public Result findTrademarkList(@PathVariable Long thirdLevelCategoryId){
        List<TrademarkDTO> list = categoryService.findTrademarkList(thirdLevelCategoryId);
        //  返回
        return Result.ok(list);
    }

    @GetMapping("/baseCategoryTrademark/findCurrentTrademarkList/{thirdLevelCategoryId}")
    public Result findCurrentTrademarkList(@PathVariable Long thirdLevelCategoryId){
        List<TrademarkDTO> list = categoryService.findUnLinkedTrademarkList(thirdLevelCategoryId);
        //  返回
        return Result.ok(list);
    }

    /**
     * 查询所有的一级分类信息
     * @return
     */
    @GetMapping("getCategory1")
    public Result<List<FirstLevelCategoryDTO>> getFirstLevelCategory() {
        List<FirstLevelCategoryDTO> firstLevelCategory = categoryService.getFirstLevelCategory();
        return Result.ok(firstLevelCategory);
    }

    /**
     * 根据一级分类Id 查询二级分类数据
     */
    @GetMapping("getCategory2/{firstLevelCategoryId}")
    public Result<List<SecondLevelCategoryDTO>> getSecondLevelCategory(@PathVariable("firstLevelCategoryId") Long firstLevelCategoryId) {
        List<SecondLevelCategoryDTO> secondLevelCategory = categoryService.getSecondLevelCategory(firstLevelCategoryId);
        return Result.ok(secondLevelCategory);
    }

    /**
     * 根据二级分类Id 查询三级分类数据
     */
    @GetMapping("getCategory3/{thirdLevelCategoryId}")
    public Result<List<ThirdLevelCategoryDTO>> getThirdLevelCategory(@PathVariable("thirdLevelCategoryId") Long secondLevelCategoryId) {
        List<ThirdLevelCategoryDTO> thirdLevelCategory = categoryService.getThirdLevelCategory(secondLevelCategoryId);
        return Result.ok(thirdLevelCategory);
    }







}
