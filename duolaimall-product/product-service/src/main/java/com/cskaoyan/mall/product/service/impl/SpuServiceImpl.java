package com.cskaoyan.mall.product.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.product.converter.dto.SpuInfoConverter;
import com.cskaoyan.mall.product.converter.dto.SpuInfoPageConverter;
import com.cskaoyan.mall.product.converter.param.SpuInfoParamConverter;
import com.cskaoyan.mall.product.dto.SpuImageDTO;
import com.cskaoyan.mall.product.dto.SpuInfoPageDTO;
import com.cskaoyan.mall.product.dto.SpuPosterDTO;
import com.cskaoyan.mall.product.dto.SpuSaleAttributeInfoDTO;
import com.cskaoyan.mall.product.mapper.*;
import com.cskaoyan.mall.product.model.*;
import com.cskaoyan.mall.product.query.SpuInfoParam;
import com.cskaoyan.mall.product.service.SpuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/09 22:29
 */
@Service
public class SpuServiceImpl implements SpuService {
    @Resource
    SpuInfoMapper spuInfoMapper;
    @Resource
    SpuInfoPageConverter spuInfoPageConverter;
    @Resource
    SpuInfoParamConverter spuInfoParamConverter;
    @Resource
    SpuImageMapper spuImageMapper;
    @Resource
    SpuSaleAttrInfoMapper spuSaleAttrInfoMapper;
    @Resource
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Resource
    SpuPosterMapper spuPosterMapper;
    @Resource
    SpuInfoConverter spuInfoConverter;
    @Resource
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public SpuInfoPageDTO getSpuInfoPage(Page<SpuInfo> pageParam, SpuInfoParam spuInfo) {
        // 构建wrapper对象
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        // 设置查询条件
        wrapper.eq("third_level_category_id", spuInfo.getCategory3Id());
        // 查询
        Page<SpuInfo> page = spuInfoMapper.selectPage(pageParam, wrapper);
        // 转换
        return spuInfoPageConverter.spuInfoPage2PageDTO(page);
    }

    @Override
    public void saveSpuInfo(SpuInfoParam spuInfoParam) {
        // 现将参数对象转化为PO对象
        SpuInfo spuInfo = spuInfoParamConverter.spuInfoParam2Info(spuInfoParam);
        // 插入基本的spu信息
        spuInfoMapper.insert(spuInfo);
        Long spuInfoId = spuInfo.getId();
        //  获取到spuImage 集合数据
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();

        // 循环遍历spuImageList
        for (SpuImage spuImage : spuImageList) {
            // 1. 给spuImage的spuId属性赋值因为只有将spu基本信息插入数据库spuInfo的id才有值
            spuImage.setSpuId(spuInfoId);
            // 2. 保存spuImage
            spuImageMapper.insert(spuImage);
        }

        //  获取销售属性集合
        List<SpuSaleAttributeInfo> spuSaleAttributeInfoList = spuInfo.getSpuSaleAttributeInfoList();

        //  判断
        if (!CollectionUtils.isEmpty(spuSaleAttributeInfoList)) {
            //  循环遍历
            for (SpuSaleAttributeInfo spuSaleAttrInfo : spuSaleAttributeInfoList) {
                // 给spuSaleAttrInfo的spuId赋值，并保存spuSaleAttrInfo到数据库
                spuSaleAttrInfo.setSpuId(spuInfoId);
                spuSaleAttrInfoMapper.insert(spuSaleAttrInfo);
                //  再此获取销售属性值集合
                List<SpuSaleAttributeValue> spuSaleAttributeValueList = spuSaleAttrInfo.getSpuSaleAttrValueList();
                // 遍历销售属性值集合spuSaleAttributeValueList
                for (SpuSaleAttributeValue spuSaleAttributeValue : spuSaleAttributeValueList) {
                    // 1. 给spuSaleAttrValue的spuId赋值
                    spuSaleAttributeValue.setSpuId(spuInfoId);
                    // 2. 给spuSaleAttrValue的spuSaleAttrId赋值
                    spuSaleAttributeValue.setSpuSaleAttrId(spuSaleAttrInfo.getId());
                    // 3. 保存spuSaleAttrValue到数据库
                    spuSaleAttrValueMapper.insert(spuSaleAttributeValue);
                }


            }
        }

        //  获取到posterList 集合数据
        List<SpuPoster> spuPosterList = spuInfo.getSpuPosterList();

        // 遍历销售属性值集合spuPosterList
        for (SpuPoster spuPoster : spuPosterList) {
            // 1. 给spuPoster的spuId赋值
            spuPoster.setSpuId(spuInfoId);
            // 2. 保存spuPoster到数据库
            spuPosterMapper.insert(spuPoster);
        }


    }

    @Override
    public List<SpuImageDTO> getSpuImageList(Long spuId) {
        QueryWrapper<SpuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        List<SpuImage> spuImageList = spuImageMapper.selectList(queryWrapper);
        return spuInfoConverter.spuImagePOs2DTOs(spuImageList);
    }

    @Override
    public List<SpuSaleAttributeInfoDTO> getSpuSaleAttrList(Long spuId) {
        QueryWrapper<SpuSaleAttributeInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        List<SpuSaleAttributeInfo> spuSaleAttributeInfos = spuSaleAttrInfoMapper.selectSpuSaleAttrList(spuId);
        return spuInfoConverter.spuSaleAttributeInfoPOs2DTOs(spuSaleAttributeInfos);
    }

    @Override
    public List<SpuPosterDTO> findSpuPosterBySpuId(Long spuId) {
        QueryWrapper<SpuPoster> spuInfoQueryWrapper = new QueryWrapper<>();
        spuInfoQueryWrapper.eq("spu_id",spuId);
        List<SpuPoster> spuPosterList = spuPosterMapper.selectList(spuInfoQueryWrapper);

        return spuInfoConverter.spuPosterPOs2DTOs(spuPosterList);
    }

    @Override
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
