package com.cskaoyan.mall.web.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.ProductDetailDTO;
import com.cskaoyan.mall.web.client.ProductApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ProductDetailController {

    @Autowired
    private ProductApiClient productApiClient;

    /**
     * sku详情页面
     */
    @RequestMapping("{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model){
        // 调用商品服务，通过skuId 查询skuInfo
        Result<ProductDetailDTO> result = productApiClient.getItem(skuId);
        // 获取服务调用的结果
        ProductDetailDTO data = result.getData();
        // 以map的形式传递渲染所需数据
        Map<String, Object> renderData = new HashMap<>();
        renderData.put("skuInfo", data.getSkuInfo());
        renderData.put("spuSaleAttrList", data.getSpuSaleAttrList());
        renderData.put("valuesSkuJson", data.getValuesSkuJson());
        renderData.put("price", data.getPrice());
        renderData.put("categoryHierarchy", data.getCategoryHierarchy());
        renderData.put("spuPosterList", data.getSpuPosterList());
        renderData.put("skuAttrList", data.getSkuAttrList());
        model.addAllAttributes(renderData);
        return "item/item";
    }
}
