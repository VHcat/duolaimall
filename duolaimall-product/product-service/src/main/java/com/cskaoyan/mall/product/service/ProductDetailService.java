package com.cskaoyan.mall.product.service;

import com.cskaoyan.mall.product.dto.ProductDetailDTO;

import java.util.Map;

public interface ProductDetailService {

    /**
     * 获取sku详情信息
     * @param skuId
     * @return
     */
    ProductDetailDTO getItemBySkuId(Long skuId);
}
