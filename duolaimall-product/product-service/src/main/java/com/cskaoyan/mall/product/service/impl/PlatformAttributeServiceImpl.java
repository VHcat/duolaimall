package com.cskaoyan.mall.product.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/07 21:42
 */
@Service
public class PlatformAttributeServiceImpl implements PlatformAttributeService {
    @Resource
    PlatformAttrInfoMapper platformAttrInfoMapper;
    @Resource
    PlatformAttrValueMapper platformAttrValueMapper;
    @Resource
    PlatformAttributeInfoConverter platformAttributeInfoConverter;
    @Resource
    PlatformAttributeInfoParamConverter platformAttributeInfoParamConverter;

    @Override
    public List<PlatformAttributeInfoDTO> getPlatformAttrInfoList(Long firstLevelCategoryId, Long secondLevelCategoryId, Long thirdLevelCategoryId) {
        List<PlatformAttributeInfo> platformAttributeInfos =
                platformAttrInfoMapper.selectPlatFormAttrInfoList(firstLevelCategoryId, secondLevelCategoryId, thirdLevelCategoryId);
        return platformAttributeInfoConverter.platformAttributeInfoPOs2DTOs(platformAttributeInfos);
    }

    @Override
    public void savePlatformAttrInfo(PlatformAttributeParam platformAttributeParam) {
        // 更改数据库中平台属性和平台属性值
//        platformAttrInfoMapper.insert(platformAttributeInfo);
//        List<PlatformAttributeValueParam> attrValueList = platformAttributeParam.getAttrValueList();
//        // 将平台属性值逐条插入到数据库中
//        for (PlatformAttributeValueParam platformAttributeValueParam : attrValueList) {
//            // 把PlatformAttributeParam对象转化为PlatformAttributeValue对象
//            PlatformAttributeValue platformAttributeValue
//                    = platformAttributeInfoParamConverter.attributeValueParam2AttributeValue(platformAttributeValueParam);
//            platformAttrValueMapper.insert(platformAttributeValue);
//        }

        // 将前端参数转化为
        PlatformAttributeInfo platformAttributeInfo
                = platformAttributeInfoParamConverter.attributeInfoParam2Info(platformAttributeParam);

        // 判断平台属性
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

        PlatformAttributeInfo platformAttributeInfo = platformAttrInfoMapper.selectById(attrId);

        return platformAttributeInfoConverter.platformAttributeInfoPO2DTO(platformAttributeInfo);
    }
}
