package com.cskaoyan.mall.product.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.common.cache.RedisCache;
import com.cskaoyan.mall.product.converter.dto.SpuInfoConverter;
import com.cskaoyan.mall.product.converter.dto.SpuInfoPageConverter;
import com.cskaoyan.mall.product.converter.param.SpuInfoParamConverter;
import com.cskaoyan.mall.product.dto.*;
import com.cskaoyan.mall.product.mapper.*;
import com.cskaoyan.mall.product.model.*;
import com.cskaoyan.mall.product.query.SpuInfoParam;
import com.cskaoyan.mall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    SpuImageMapper spuImageMapper;

    @Autowired
    SpuSaleAttrInfoMapper spuSaleAttrInfoMapper;

    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SpuPosterMapper spuPosterMapper;

    @Autowired
    SpuInfoConverter spuInfoConverter;

    @Autowired
    SpuInfoPageConverter spuInfoPageConverter;

    @Autowired
    SpuInfoParamConverter spuInfoParamConverter;


    @Override
    public SpuInfoPageDTO getSpuInfoPage(Page<SpuInfo> pageParam, SpuInfoParam spuInfo) {
        // 获取指定类目下对应的spu
        LambdaQueryWrapper<SpuInfo> spuInfoQueryWrapper = new LambdaQueryWrapper<>();
        spuInfoQueryWrapper.eq(SpuInfo::getThirdLevelCategoryId, spuInfo.getCategory3Id());
        spuInfoQueryWrapper.orderByDesc(SpuInfo::getId);
        Page<SpuInfo> spuInfoPage = spuInfoMapper.selectPage(pageParam, spuInfoQueryWrapper);

        return spuInfoPageConverter.spuInfoPage2PageDTO(spuInfoPage);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(SpuInfoParam spuInfoParam) {
         /*
            spuInfo;
            spuImage;
            spuSaleAttr;
            spuSaleAttrValue;
            spuPoster
     */
        SpuInfo spuInfo = spuInfoParamConverter.spuInfoParam2Info(spuInfoParam);
        spuInfoMapper.insert(spuInfo);

        //  获取到spuImage 集合数据
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        //  判断不为空
        if (!CollectionUtils.isEmpty(spuImageList)) {
            //  循环遍历
            for (SpuImage spuImage : spuImageList) {
                //  需要将spuId 赋值
                spuImage.setSpuId(spuInfo.getId());
                //  保存spuImge
                spuImageMapper.insert(spuImage);
            }
        }
        //  获取销售属性集合
        List<SpuSaleAttributeInfo> spuSaleAttributeInfoList = spuInfo.getSpuSaleAttributeInfoList();
        //  判断
        if (!CollectionUtils.isEmpty(spuSaleAttributeInfoList)) {
            //  循环遍历
            for (SpuSaleAttributeInfo spuSaleAttrInfo : spuSaleAttributeInfoList) {
                //  需要将spuId 赋值
                spuSaleAttrInfo.setSpuId(spuInfo.getId());
                spuSaleAttrInfoMapper.insert(spuSaleAttrInfo);

                //  再此获取销售属性值集合
                List<SpuSaleAttributeValue> spuSaleAttributeValueList = spuSaleAttrInfo.getSpuSaleAttrValueList();
                //  判断
                if (!CollectionUtils.isEmpty(spuSaleAttributeValueList)) {
                    //  循环遍历
                    for (SpuSaleAttributeValue spuSaleAttrValue : spuSaleAttributeValueList) {
                        //   需要将spuId， spu_sale_attr_id 赋值
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSpuSaleAttrId(spuSaleAttrInfo.getId());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }

        //  获取到posterList 集合数据
        List<SpuPoster> spuPosterList = spuInfo.getSpuPosterList();
        //  判断不为空
        if (!CollectionUtils.isEmpty(spuPosterList)) {
            for (SpuPoster spuPoster : spuPosterList) {
                //  需要将spuId 赋值
                spuPoster.setSpuId(spuInfo.getId());
                //  保存spuPoster
                spuPosterMapper.insert(spuPoster);
            }
        }

    }

    @Override
    public List<SpuImageDTO> getSpuImageList(Long spuId) {
        // 根据spu 查询所有spu所属的图片
        LambdaQueryWrapper<SpuImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpuImage::getSpuId, spuId);
        List<SpuImage> spuImages = spuImageMapper.selectList(queryWrapper);

        return spuInfoConverter.spuImagePOs2DTOs(spuImages);
    }

    @Override
    public List<SpuSaleAttributeInfoDTO> getSpuSaleAttrList(Long spuId) {
        List<SpuSaleAttributeInfo> spuSaleAttributeInfos = spuSaleAttrInfoMapper.selectSpuSaleAttrList(spuId);
        return spuInfoConverter.spuSaleAttributeInfoPOs2DTOs(spuSaleAttributeInfos);
    }

    @Override
    @RedisCache(prefix = "SpuPosterList:")
    public List<SpuPosterDTO> findSpuPosterBySpuId(Long spuId) {
        QueryWrapper<SpuPoster> spuInfoQueryWrapper = new QueryWrapper<>();
        spuInfoQueryWrapper.eq("spu_id",spuId);
        List<SpuPoster> spuPosterList = spuPosterMapper.selectList(spuInfoQueryWrapper);

        return spuInfoConverter.spuPosterPOs2DTOs(spuPosterList);
    }

    @Override
    @RedisCache(prefix = "skuValueIdsMap:")
    public Map<String, Long> getSkuValueIdsMap(Long spuId) {
        // key = 125|123 ,value = 37
        List<SkuSaleAttributeValuePermutation> permutationList = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);
        HashMap<String, Long> valueIdsMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(permutationList)) {
            permutationList.forEach(singlePermutation -> {
                valueIdsMap.put(singlePermutation.getSkuSaleAttrValuePermutation()
                        , singlePermutation.getSkuId());
            });
        }

        return valueIdsMap;
    }
}
