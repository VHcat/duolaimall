package com.cskaoyan.mall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.SkuInfoPageDTO;
import com.cskaoyan.mall.product.model.SkuInfo;
import com.cskaoyan.mall.product.query.SkuInfoParam;
import com.cskaoyan.mall.product.service.SkuService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/12 21:09
 */
@RestController
@RequestMapping("/admin/product")
public class SkuController {
    @Resource
    SkuService skuService;

    @GetMapping("/list/{page}/{size}")
    public Result getList(@PathVariable Long page, @PathVariable Long size) {
        Page<SkuInfo> skuInfo = new Page<>(page, size);
        SkuInfoPageDTO skuInfoPageDTO = skuService.getPage(skuInfo);
        return Result.ok(skuInfoPageDTO);
    }

    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfoParam skuInfo) {
        // 调用服务层
        skuService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId) {
        skuService.onSale(skuId);
        return Result.ok();
    }

    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId) {
        skuService.offSale(skuId);
        return Result.ok();
    }
}
