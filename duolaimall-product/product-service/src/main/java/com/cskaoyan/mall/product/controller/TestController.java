package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.TestSkuProductDTO;
import com.cskaoyan.mall.product.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 北海 on 2023-06-05 15:59
 */
@RestController
public class TestController {

    @Autowired
    TestService testService;

    @GetMapping("/admin/product/{skuId}")
    public Result getSkuProduct(@PathVariable("skuId") Long skuId) {
        TestSkuProductDTO skuProductInfo = testService.getSkuProductInfo(skuId);
        return Result.ok(skuProductInfo);
    }
}
