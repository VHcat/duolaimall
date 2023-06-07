package com.cskaoyan.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cskaoyan.mall.product.model.PlatformAttributeInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlatformAttrInfoMapper extends BaseMapper<PlatformAttributeInfo> {

    /**
     * 根据分类Id 查询平台属性集合对象
     */
    List<PlatformAttributeInfo> selectPlatFormAttrInfoList(@Param("firstLevelCategoryId") Long firstLevelCategoryId
            , @Param("secondLevelCategoryId") Long secondLevelCategoryId, @Param("thirdLevelCategoryId") Long thirdLevelCategoryId);

    List<PlatformAttributeInfo> selectPlatformAttrInfoListBySkuId(Long skuId);

}
