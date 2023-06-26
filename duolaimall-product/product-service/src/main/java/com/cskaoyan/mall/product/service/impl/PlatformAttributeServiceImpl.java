package com.cskaoyan.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cskaoyan.mall.product.converter.dto.PlatformAttributeInfoConverter;
import com.cskaoyan.mall.product.converter.param.PlatformAttributeInfoParamConverter;
import com.cskaoyan.mall.product.dto.PlatformAttributeInfoDTO;
import com.cskaoyan.mall.product.mapper.PlatformAttrInfoMapper;
import com.cskaoyan.mall.product.mapper.PlatformAttrValueMapper;
import com.cskaoyan.mall.product.model.PlatformAttributeInfo;
import com.cskaoyan.mall.product.model.PlatformAttributeValue;
import com.cskaoyan.mall.product.query.PlatformAttributeParam;
import com.cskaoyan.mall.product.service.PlatformAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class PlatformAttributeServiceImpl implements PlatformAttributeService {

    @Autowired
    PlatformAttrInfoMapper platformAttrInfoMapper;
    @Autowired
    PlatformAttrValueMapper platformAttrValueMapper;

    @Autowired
    PlatformAttributeInfoConverter platformAttributeInfoConverter;

    @Autowired
    PlatformAttributeInfoParamConverter platformAttributeInfoParamConverter;


    @Override
    //@RedisCache(prefix = "platformAttrInfoList:")
    public List<PlatformAttributeInfoDTO> getPlatformAttrInfoList(Long firstLevelCategoryId
            , Long secondLevelCategoryId, Long thirdLevelCategoryId) {
        List<PlatformAttributeInfo> platformAttributeInfos = platformAttrInfoMapper.selectPlatFormAttrInfoList(firstLevelCategoryId, secondLevelCategoryId, thirdLevelCategoryId);
        return platformAttributeInfoConverter.platformAttributeInfoPOs2DTOs(platformAttributeInfos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePlatformAttrInfo(PlatformAttributeParam platformAttributeParam) {
        // 将前端参数转化为
        PlatformAttributeInfo platformAttributeInfo
                = platformAttributeInfoParamConverter.attributeInfoParam2Info(platformAttributeParam);

        // baseAttrInfo
        if (platformAttributeInfo.getId() != null) {
            // 修改数据
            platformAttrInfoMapper.updateById(platformAttributeInfo);
        } else {
            // 新增
            platformAttrInfoMapper.insert(platformAttributeInfo);
        }

        // platformAttrValue平台属性值，先删除，在新增的方式！
        LambdaQueryWrapper<PlatformAttributeValue> platformAttributeValueQueryWrapper = new LambdaQueryWrapper<>();
        // 删除平台属性原本在数据库中对应的属性值
        platformAttributeValueQueryWrapper.eq(PlatformAttributeValue::getAttrId, platformAttributeInfo.getId());
        platformAttrValueMapper.delete(platformAttributeValueQueryWrapper);

        // 获取页面传递过来的所有平台属性值数据
        List<PlatformAttributeValue> attrValueList = platformAttributeInfo.getAttrValueList();
        if (!CollectionUtils.isEmpty(attrValueList)) {
            // 循环遍历
            for (PlatformAttributeValue platformAttributeValue : attrValueList) {
                // 获取平台属性Id 给attrId
                platformAttributeValue.setAttrId(platformAttributeInfo.getId());
                platformAttrValueMapper.insert(platformAttributeValue);
            }
        }
    }

    @Override
    public PlatformAttributeInfoDTO getPlatformAttrInfo(Long attrId) {
        // 根据id查询平台属性
        PlatformAttributeInfo platformAttributeInfo = platformAttrInfoMapper.selectById(attrId);

        // 根据平台属性查询对应的属性值
        LambdaQueryWrapper<PlatformAttributeValue> platformAttributeValueQueryWrapper = new LambdaQueryWrapper<>();
        platformAttributeValueQueryWrapper.eq(PlatformAttributeValue::getAttrId, attrId);
        List<PlatformAttributeValue> platformAttributeValues = platformAttrValueMapper.selectList(platformAttributeValueQueryWrapper);

        // 查询到最新的平台属性值集合数据放入平台属性中！
        platformAttributeInfo.setAttrValueList(platformAttributeValues);

        return platformAttributeInfoConverter.platformAttributeInfoPO2DTO(platformAttributeInfo);
    }
}
