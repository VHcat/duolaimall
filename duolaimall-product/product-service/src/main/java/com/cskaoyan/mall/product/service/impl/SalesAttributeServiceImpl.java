package com.cskaoyan.mall.product.service.impl;

import com.cskaoyan.mall.product.converter.dto.SaleAttributeInfoConverter;
import com.cskaoyan.mall.product.dto.SaleAttributeInfoDTO;
import com.cskaoyan.mall.product.mapper.SaleAttrInfoMapper;
import com.cskaoyan.mall.product.model.SaleAttributeInfo;
import com.cskaoyan.mall.product.service.SalesAttributeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/12 20:01
 */
@Service
public class SalesAttributeServiceImpl implements SalesAttributeService {
    @Resource
    SaleAttrInfoMapper saleAttrInfoMapper;
    @Resource
    SaleAttributeInfoConverter saleAttributeInfoConverter;
    @Override
    public List<SaleAttributeInfoDTO> getSaleAttrInfoList() {
        List<SaleAttributeInfo> saleAttributeInfos = saleAttrInfoMapper.selectList(null);
        // 将查询结果转换为DTO
        return saleAttributeInfoConverter.saleAttributeInfoPOs2DTOs(saleAttributeInfos);
    }
}
