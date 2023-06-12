package com.cskaoyan.mall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.SpuImageDTO;
import com.cskaoyan.mall.product.dto.SpuInfoPageDTO;
import com.cskaoyan.mall.product.dto.SpuSaleAttributeInfoDTO;
import com.cskaoyan.mall.product.model.SpuInfo;
import com.cskaoyan.mall.product.query.SpuInfoParam;
import com.cskaoyan.mall.product.service.SalesAttributeService;
import com.cskaoyan.mall.product.service.SpuService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/09 09:16
 */
@RestController
@RequestMapping("/admin/product")
public class SpuController {
    @Resource
    SpuService spuService;
    @Resource
    SalesAttributeService salesAttributeService;

    @GetMapping("/{page}/{size}")
    public Result getSpuInfoPage(@PathVariable Long page, @PathVariable Long size, Long category3Id) {
        Page<SpuInfo> spuInfoPage = new Page<>(page, size);
        SpuInfoParam spuInfoParam = new SpuInfoParam();
        spuInfoParam.setCategory3Id(category3Id);
        SpuInfoPageDTO spuInfoPageDTO = spuService.getSpuInfoPage(spuInfoPage, spuInfoParam);
        return Result.ok(spuInfoPageDTO);
    }

    /**
     * SPU的保存
     *
     * @param spuInfoParam
     * @return Result
     */
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfoParam spuInfoParam) {
        spuService.saveSpuInfo(spuInfoParam);
        return Result.ok();
    }

    @GetMapping("/baseSaleAttrList")
    public Result SaleAttrList() {
        // 调用业务层
        return Result.ok(salesAttributeService.getSaleAttrInfoList());
    }

    /**
     * 商品图片的回显
     *
     * @param spuId
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result<List<SpuImageDTO>> getSpuImageList(@PathVariable("spuId") Long spuId) {
        List<SpuImageDTO> spuImageList = spuService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }

    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result<List<SpuSaleAttributeInfoDTO>> getSpuSaleAttrList(@PathVariable("spuId") Long spuId) {
        List<SpuSaleAttributeInfoDTO> spuSaleAttrList = spuService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }
}
