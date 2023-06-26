package com.cskaoyan.mall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.*;
import com.cskaoyan.mall.product.model.SpuInfo;
import com.cskaoyan.mall.product.query.SpuInfoParam;
import com.cskaoyan.mall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product")
public class SpuManageController {

    @Autowired
    private SpuService spuService;


    @GetMapping("{page}/{size}")
    public Result getSpuInfoPage(@PathVariable Long page,
                                 @PathVariable Long size,
                                 SpuInfoParam spuInfoParam){
        Page<SpuInfo> spuInfoPage = new Page<>(page,size);
        // 获取数据
        SpuInfoPageDTO spuInfoPageDTO = spuService.getSpuInfoPage(spuInfoPage, spuInfoParam);
        // 将获取到的数据返回即可！
        return Result.ok(spuInfoPageDTO);
    }



    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfoParam spuInfoParam){
        // 调用服务层的保存方法
        spuService.saveSpuInfo(spuInfoParam);
        return Result.ok();
    }

    /**
     * 根据spuId 查询spuImageList
     */
    @GetMapping("spuImageList/{spuId}")
    public Result<List<SpuImageDTO>> getSpuImageList(@PathVariable("spuId") Long spuId) {
        List<SpuImageDTO> spuImageList = spuService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }


    /**
     * 根据spuId 查询销售属性集合
     */
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result<List<SpuSaleAttributeInfoDTO>> getSpuSaleAttrList(@PathVariable("spuId") Long spuId) {
        List<SpuSaleAttributeInfoDTO> spuSaleAttrList = spuService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }
}
