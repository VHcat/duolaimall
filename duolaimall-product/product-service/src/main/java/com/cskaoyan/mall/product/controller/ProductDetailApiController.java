package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.ProductDetailDTO;
import com.cskaoyan.mall.product.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/item")
public class ProductDetailApiController {


    @Autowired
    private ProductDetailService productDetailService;

    /**
     * 获取sku详情信息
     */
    @GetMapping("{skuId}")
    Result<ProductDetailDTO> getItem(@PathVariable Long skuId){
        ProductDetailDTO productDetailDTO = productDetailService.getItemBySkuId(skuId);
        return Result.ok(productDetailDTO);
    }


}
