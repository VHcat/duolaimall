package com.cskaoyan.mall.product.service;

import com.cskaoyan.mall.product.dto.PlatformAttributeInfoDTO;
import com.cskaoyan.mall.product.query.PlatformAttributeParam;

import java.util.List;

public interface PlatformAttributeService {

    /**<pre>
     * 根据分类Id 获取平台属性数据
     * 接口说明：
     * 1，平台属性可以挂在一级分类、二级分类和三级分类
     * 2，查询一级分类下面的平台属性，传：firstlevelCatogoryId，0，0；   取出该分类的平台属性
     * 3，查询二级分类下面的平台属性，传：firstlevelCatogoryId，category2Id，0；
     * 取出对应一级分类下面的平台属性与二级分类对应的平台属性
     * 4，查询三级分类下面的平台属性，传：firstlevelCatogoryId，category2Id，category3Id；
     * 取出对应一级分类、二级分类与三级分类对应的平台属性
     * </pre>
     */
    List<PlatformAttributeInfoDTO> getPlatformAttrInfoList(Long firstLevelCategoryId, Long secondLevelCategoryId, Long thirdLevelCategoryId);

    void savePlatformAttrInfo(PlatformAttributeParam platformAttributeParam);

    PlatformAttributeInfoDTO getPlatformAttrInfo(Long attrId);

}
