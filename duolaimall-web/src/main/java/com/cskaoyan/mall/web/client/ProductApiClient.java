package com.cskaoyan.mall.web.client;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.ProductDetailDTO;
import com.cskaoyan.mall.product.dto.SkuInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-product")
public interface ProductApiClient {

    /**
     *  根据skuid获取对应商品的详情页数据
     */
    @GetMapping("/api/item/{skuId}")
    Result<ProductDetailDTO> getItem(@PathVariable("skuId") Long skuId);


    /**
     * 功能描述: 根据skuId获取 sku基本信息与sku图片列表
     */
    @GetMapping("api/product/inner/getSkuInfo/{skuId}")
    SkuInfoDTO getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 获取全部分类信息
     * @return
     */
    @GetMapping("/api/product/getCategoryList")
    Result getCategoryList();
}
