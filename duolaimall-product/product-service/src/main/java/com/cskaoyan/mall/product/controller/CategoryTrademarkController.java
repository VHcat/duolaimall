package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.TrademarkDTO;
import com.cskaoyan.mall.product.query.CategoryTrademarkParam;
import com.cskaoyan.mall.product.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/08 19:58
 */
@RestController
@RequestMapping("/admin/product")
public class CategoryTrademarkController {
    @Resource
    CategoryService categoryService;

    @GetMapping("/baseCategoryTrademark/findTrademarkList/{thirdLevelCategoryId}")
    public Result findTrademarkList(@PathVariable Long thirdLevelCategoryId) {
        List<TrademarkDTO> trademarkList = categoryService.findTrademarkList(thirdLevelCategoryId);
        return Result.ok(trademarkList);
    }

    @PostMapping("/baseCategoryTrademark/save")
    public Result save(@RequestBody CategoryTrademarkParam categoryTrademarkVo) {
        categoryService.save(categoryTrademarkVo);
        return Result.ok();
    }

    @GetMapping("/baseCategoryTrademark/findCurrentTrademarkList/{thirdLevelCategoryId}")
    public Result findCurrentTrademarkList(@PathVariable Long thirdLevelCategoryId) {
        List<TrademarkDTO> unLinkedTrademarkList = categoryService.findUnLinkedTrademarkList(thirdLevelCategoryId);
        return Result.ok(unLinkedTrademarkList);
    }

    @DeleteMapping("admin/product/baseCategoryTrademark/remove/{thirdLevelCategoryId}/{trademarkId}")
    public Result remove(@PathVariable Long thirdLevelCategoryId, @PathVariable Long trademarkId) {
        categoryService.remove(thirdLevelCategoryId, trademarkId);
        return Result.ok();
    }
}
