package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.FirstLevelCategoryDTO;
import com.cskaoyan.mall.product.dto.SecondLevelCategoryDTO;
import com.cskaoyan.mall.product.dto.ThirdLevelCategoryDTO;
import com.cskaoyan.mall.product.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/07 15:36
 */
@RestController
@RequestMapping("/admin/product")
public class CategoryController {
    @Resource
    CategoryService categoryService;
    
    @GetMapping("/getCategory1")
    public Result getCategory1() {
        List<FirstLevelCategoryDTO> firstLevelCategory = categoryService.getFirstLevelCategory();
        return Result.ok(firstLevelCategory);
    }

    // 查询二级类目
    @GetMapping("/getCategory2/{firstLevelCategoryId}")
    public Result getCategory2(@PathVariable long firstLevelCategoryId) {
        List<SecondLevelCategoryDTO> secondLevelCategory = categoryService.getSecondLevelCategory(firstLevelCategoryId);
        return Result.ok(secondLevelCategory);
    }

    @GetMapping("/getCategory3/{SecondLevelCategoryId}")
    public Result gerCategory3(@PathVariable long SecondLevelCategoryId) {
        List<ThirdLevelCategoryDTO> thirdLevelCategory = categoryService.getThirdLevelCategory(SecondLevelCategoryId);
        return Result.ok(thirdLevelCategory);
    }
}
