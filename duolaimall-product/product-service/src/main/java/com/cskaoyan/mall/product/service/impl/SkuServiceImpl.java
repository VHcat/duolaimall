package com.cskaoyan.mall.product.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.product.converter.dto.PlatformAttributeInfoConverter;
import com.cskaoyan.mall.product.converter.dto.SkuInfoConverter;
import com.cskaoyan.mall.product.converter.dto.SkuInfoPageConverter;
import com.cskaoyan.mall.product.converter.dto.SpuInfoConverter;
import com.cskaoyan.mall.product.converter.param.SkuInfoParamConverter;
import com.cskaoyan.mall.product.dto.PlatformAttributeInfoDTO;
import com.cskaoyan.mall.product.dto.SkuInfoDTO;
import com.cskaoyan.mall.product.dto.SkuInfoPageDTO;
import com.cskaoyan.mall.product.dto.SpuSaleAttributeInfoDTO;
import com.cskaoyan.mall.product.mapper.*;
import com.cskaoyan.mall.product.model.*;
import com.cskaoyan.mall.product.query.SkuInfoParam;
import com.cskaoyan.mall.product.service.SkuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/12 21:10
 */
@Service
public class SkuServiceImpl implements SkuService {
    @Resource
    SkuInfoMapper skuInfoMapper;
    @Resource
    SkuInfoPageConverter skuInfoPageConverter;
    @Resource
    SkuInfoParamConverter skuInfoParamConverter;
    @Resource
    SkuImageMapper skuImageMapper;
    @Resource
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Resource
    SkuPlatformAttrValueMapper skuPlatformAttrValueMapper;
    @Resource
    SkuInfoConverter skuInfoConverter;
    @Resource
    SpuSaleAttrInfoMapper spuSaleAttrInfoMapper;
    @Resource
    SpuInfoConverter spuInfoConverter;
    @Resource
    PlatformAttrInfoMapper platformAttrInfoMapper;
    @Resource
    PlatformAttributeInfoConverter platformAttributeInfoConverter;


    @Override
    public void saveSkuInfo(SkuInfoParam skuInfoParam) {
        /*
      	 1. 保存SKU基本信息
      	 2. 保存SKU图片
      	 3. 保存销售属性值
      	 4. 保存平台属性值
        */

        // 将sku参数对象，转化为PO对象 
        SkuInfo skuInfo = skuInfoParamConverter.SkuInfoParam2Info(skuInfoParam);

        // 保存sku基本信息保存到sku_info
        skuInfoMapper.insert(skuInfo);
        // 获取sku图片列表
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size() > 0) {
            // 循环遍历
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                // 保存sku的多张图片信息, 保存到sku_img
                skuImageMapper.insert(skuImage);
            }
        }

        List<SkuSaleAttributeValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttributeValueList();
        // 调用判断集合方法
        if (!CollectionUtils.isEmpty(skuSaleAttrValueList)) {
            for (SkuSaleAttributeValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                // 保存sku销售属性值
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }

        List<SkuPlatformAttributeValue> skuAttrValueList = skuInfo.getSkuPlatformAttributeValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (SkuPlatformAttributeValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                // 保存sku平台属性值
                skuPlatformAttrValueMapper.insert(skuAttrValue);
            }
        }
    }

    @Override
    public SkuInfoPageDTO getPage(Page<SkuInfo> pageParam) {
        Page<SkuInfo> skuInfoPage = skuInfoMapper.selectPage(pageParam, null);
        return skuInfoPageConverter.skuInfoPagePO2PageDTO(skuInfoPage);
    }

    /**
     * 将is_sale设置为<font color=red>1</font>视为上架
     *
     * @param skuId 库存单元表的主键
     */
    @Override
    public void onSale(Long skuId) {
        UpdateWrapper<SkuInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", skuId);
        updateWrapper.set("is_sale", 1);
        skuInfoMapper.update(null, updateWrapper);
    }

    /**
     * 将is_sale设置为<font color=red>0</font> 视为下架
     *
     * @param skuId 库存单元表的主键
     */
    @Override
    public void offSale(Long skuId) {
        UpdateWrapper<SkuInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", skuId);
        updateWrapper.set("is_sale", 0);
        skuInfoMapper.update(null, updateWrapper);
    }

    @Override
    public SkuInfoDTO getSkuInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        List<SkuImage> skuImageList = skuImageMapper.getSkuImages(skuId);
        skuInfo.setSkuImageList(skuImageList);
        return skuInfoConverter.skuInfoPO2DTO(skuInfo);
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (null != skuInfo) {
            return skuInfo.getPrice();
        }
        return new BigDecimal("0");

    }

    @Override
    public List<SpuSaleAttributeInfoDTO> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        List<SpuSaleAttributeInfo> spuSaleAttributeInfos = spuSaleAttrInfoMapper.selectSpuSaleAttrListCheckedBySku(skuId, spuId);
        return spuInfoConverter.spuSaleAttributeInfoPOs2DTOs(spuSaleAttributeInfos);
    }

    @Override
    public List<PlatformAttributeInfoDTO> getPlatformAttrInfoBySku(Long skuId) {
        List<PlatformAttributeInfo> platformAttributeInfos = platformAttrInfoMapper.selectPlatformAttrInfoListBySkuId(skuId);
        return platformAttributeInfoConverter.platformAttributeInfoPOs2DTOs(platformAttributeInfos);
    }
}
