package com.cskaoyan.mall.product.service.impl;

import com.cskaoyan.mall.product.converter.dto.TestSkuProductConverter;
import com.cskaoyan.mall.product.dto.TestSkuProductDTO;
import com.cskaoyan.mall.product.mapper.SkuInfoMapper;
import com.cskaoyan.mall.product.model.SkuInfo;
import com.cskaoyan.mall.product.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 北海 on 2023-06-05 14:49
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    TestSkuProductConverter converter;


    @Override
    public TestSkuProductDTO getSkuProductInfo(Long skuId) {

        // 访问数据库查询指定skuId对应的商品数据
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        TestSkuProductDTO testSkuProductDTO
                = converter.skuInfo2TestSkuProductDTO(skuInfo);
        return testSkuProductDTO;
    }
}
