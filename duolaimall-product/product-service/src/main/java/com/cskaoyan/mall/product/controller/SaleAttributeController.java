package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.SaleAttributeInfoDTO;
import com.cskaoyan.mall.product.service.SalesAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/product")
public class SaleAttributeController {

    @Autowired
    private SalesAttributeService salesAttributeService;

    @GetMapping("baseSaleAttrList")
    public Result SaleAttrList(){
        // 查询所有的销售属性集合
        List<SaleAttributeInfoDTO> saleAttrInfoList = salesAttributeService.getSaleAttrInfoList();
        return Result.ok(saleAttrInfoList);
    }
}
