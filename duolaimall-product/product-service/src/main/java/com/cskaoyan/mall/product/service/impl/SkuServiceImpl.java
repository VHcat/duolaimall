package com.cskaoyan.mall.product.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.common.cache.RedisCache;
import com.cskaoyan.mall.common.constant.RedisConst;
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
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SkuPlatformAttrValueMapper skuPlatformAttrValueMapper;

    @Autowired
    SpuSaleAttrInfoMapper spuSaleAttrInfoMapper;

    @Autowired
    PlatformAttrInfoMapper platformAttrInfoMapper;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SkuInfoPageConverter skuInfoPageConverter;
    @Autowired
    SkuInfoConverter skuInfoConverter;

    @Autowired
    SpuInfoConverter spuInfoConverter;
    @Autowired
    PlatformAttributeInfoConverter platformAttributeInfoConverter;

    @Autowired
    SkuInfoParamConverter skuInfoParamConverter;



    @Override
    public void saveSkuInfo(SkuInfoParam skuInfoParam) {

        SkuInfo skuInfo = skuInfoParamConverter.SkuInfoParam2Info(skuInfoParam);

        // 保存sku基本信息
        skuInfoMapper.insert(skuInfo);
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size() > 0) {
            // 循环遍历
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                // 保存sku的多张图片信息
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
        LambdaQueryWrapper<SkuInfo> skuInfoQueryWrapper = new LambdaQueryWrapper<>();
        skuInfoQueryWrapper.orderByDesc(SkuInfo::getId);

        Page<SkuInfo> skuInfoPage = skuInfoMapper.selectPage(pageParam, skuInfoQueryWrapper);
        return skuInfoPageConverter.skuInfoPagePO2PageDTO(skuInfoPage);
    }

    @Override
    public void onSale(Long skuId) {
        // 更改销售状态
        SkuInfo skuInfoUp = new SkuInfo();
        skuInfoUp.setId(skuId);
        skuInfoUp.setIsSale(1);
        skuInfoMapper.updateById(skuInfoUp);

        //添加布隆过滤
        RBloomFilter<Long> rbloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
        rbloomFilter.add(skuId);

        // 未完成
        // 发送消息，在es中添加该商品信息

    }

    @Override
    public void offSale(Long skuId) {
        // 更改销售状态
        SkuInfo skuInfoUp = new SkuInfo();
        skuInfoUp.setId(skuId);
        skuInfoUp.setIsSale(0);
        skuInfoMapper.updateById(skuInfoUp);

        // 发送消息，在es中添加该商品信息

    }

    @RedisCache(prefix = RedisConst.SKUKEY_PREFIX)
    @Override
    public SkuInfoDTO getSkuInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        // 根据skuId 查询图片列表集合
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

    @RedisCache(prefix = "spuSaleAttrListCheckBySku:")
    @Override
    public List<SpuSaleAttributeInfoDTO> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        List<SpuSaleAttributeInfo> spuSaleAttributeInfos = spuSaleAttrInfoMapper.selectSpuSaleAttrListCheckedBySku(skuId, spuId);
        return spuInfoConverter.spuSaleAttributeInfoPOs2DTOs(spuSaleAttributeInfos);
    }

    @RedisCache(prefix = "platformAttributeInfoList:")
    @Override
    public List<PlatformAttributeInfoDTO> getPlatformAttrInfoBySku(Long skuId) {
        List<PlatformAttributeInfo> platformAttributeInfos = platformAttrInfoMapper.selectPlatformAttrInfoListBySkuId(skuId);
        List<PlatformAttributeInfoDTO> platformAttributeInfoDTOs = platformAttributeInfoConverter.platformAttributeInfoPOs2DTOs(platformAttributeInfos);
        return platformAttributeInfoDTOs;
    }
}
