package com.cskaoyan.mall.product.service.impl;

import com.cskaoyan.mall.product.converter.dto.SaleAttributeInfoConverter;
import com.cskaoyan.mall.product.dto.SaleAttributeInfoDTO;
import com.cskaoyan.mall.product.mapper.SaleAttrInfoMapper;
import com.cskaoyan.mall.product.model.SaleAttributeInfo;
import com.cskaoyan.mall.product.service.SalesAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleAttributeServiceImpl implements SalesAttributeService {

    @Autowired
    SaleAttrInfoMapper saleAttrInfoMapper;

    @Autowired
    SaleAttributeInfoConverter saleAttributeInfoConverter;

    @Override
    public List<SaleAttributeInfoDTO> getSaleAttrInfoList() {
        List<SaleAttributeInfo> saleAttributeInfos = saleAttrInfoMapper.selectList(null);
        return  saleAttributeInfoConverter.saleAttributeInfoPOs2DTOs(saleAttributeInfos);
    }
}
