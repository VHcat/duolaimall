package com.cskaoyan.mall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.SkuInfoPageDTO;
import com.cskaoyan.mall.product.model.SkuInfo;
import com.cskaoyan.mall.product.query.SkuInfoParam;
import com.cskaoyan.mall.product.service.SkuService;
import com.cskaoyan.mall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/product")
public class SkuManageController {

    @Autowired
    private SpuService spuService;

    @Autowired
    private SkuService skuService;

    /**
     * 保存sku
     */
    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfoParam skuInfo) {
        // 调用服务层
        skuService .saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @GetMapping("/list/{page}/{limit}")
    public Result index(
            @PathVariable Long page,
            @PathVariable Long limit) {

        Page<SkuInfo> pageParam = new Page<>(page, limit);
        SkuInfoPageDTO skuInfoPageDTO = skuService.getPage(pageParam);
        return Result.ok(skuInfoPageDTO);
    }

    /**
     * 商品上架
     */
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId) {
        skuService.onSale(skuId);
        return Result.ok();
    }

    /**
     * 商品下架
     */
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId) {
        skuService.offSale(skuId);
        return Result.ok();
    }


}
