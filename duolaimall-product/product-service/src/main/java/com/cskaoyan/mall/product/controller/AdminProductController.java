package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.FirstLevelCategoryDTO;
import com.cskaoyan.mall.product.dto.PlatformAttributeInfoDTO;
import com.cskaoyan.mall.product.dto.SecondLevelCategoryDTO;
import com.cskaoyan.mall.product.dto.ThirdLevelCategoryDTO;
import com.cskaoyan.mall.product.query.PlatformAttributeParam;
import com.cskaoyan.mall.product.service.CategoryService;
import com.cskaoyan.mall.product.service.PlatformAttributeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/07 15:36
 */
@RestController
@RequestMapping("/admin/product")
public class AdminProductController {
    @Resource
    CategoryService categoryService;
    @Resource
    PlatformAttributeService platformAttributeService;

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

    @GetMapping("/attrInfoList/{firstLevelCategoryId}/{secondLevelCategoryId}/{thirdLevelCategoryId}")
    public Result getAttrInfoList(@PathVariable Long firstLevelCategoryId,
                                  @PathVariable Long secondLevelCategoryId,
                                  @PathVariable Long thirdLevelCategoryId) {
        List<PlatformAttributeInfoDTO> platformAttrInfoList
                = platformAttributeService.getPlatformAttrInfoList(firstLevelCategoryId, secondLevelCategoryId, thirdLevelCategoryId);
        return Result.ok(platformAttrInfoList);
    }

    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody PlatformAttributeParam platformAttributeParam) {
        platformAttributeService.savePlatformAttrInfo(platformAttributeParam);
        return Result.ok();
    }

}
