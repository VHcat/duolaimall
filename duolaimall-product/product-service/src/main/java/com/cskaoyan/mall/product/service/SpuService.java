package com.cskaoyan.mall.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.product.dto.*;
import com.cskaoyan.mall.product.model.SpuInfo;
import com.cskaoyan.mall.product.query.SpuInfoParam;

import java.util.List;
import java.util.Map;

public interface SpuService {

    /*
         根据分页参数查询SPU分页数据
     */
    SpuInfoPageDTO getSpuInfoPage(Page<SpuInfo> pageParam, SpuInfoParam spuInfo);

    /*
        保存完整的SPU信息
     */
    void saveSpuInfo(SpuInfoParam spuInfo);

    /*
         根据spuId获取指定SPU中包含的图片集合
     */
    List<SpuImageDTO> getSpuImageList(Long spuId);

    /*
         根据SpuId查询SPU中包含的销售属性以及销售属性值集合
     */
    List<SpuSaleAttributeInfoDTO> getSpuSaleAttrList(Long spuId);

    List<SpuPosterDTO> findSpuPosterBySpuId(Long spuId);

    Map<String, Long> getSkuValueIdsMap(Long spuId);
}
