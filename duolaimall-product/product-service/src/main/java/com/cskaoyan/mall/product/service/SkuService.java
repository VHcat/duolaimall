package com.cskaoyan.mall.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.product.dto.PlatformAttributeInfoDTO;
import com.cskaoyan.mall.product.dto.SkuInfoDTO;
import com.cskaoyan.mall.product.dto.SkuInfoPageDTO;
import com.cskaoyan.mall.product.dto.SpuSaleAttributeInfoDTO;
import com.cskaoyan.mall.product.model.SkuInfo;
import com.cskaoyan.mall.product.query.SkuInfoParam;

import java.math.BigDecimal;
import java.util.List;

public interface SkuService {

    /*
         保存SKU
     */
    void saveSkuInfo(SkuInfoParam skuInfo);

    /*
         根据分页参数查询SKU分页数据
     */
    SkuInfoPageDTO getPage(Page<SkuInfo> pageParam);

    /*
         SKU商品上架
     */
    void onSale(Long skuId);

    /*
        SKU商品下架
     */
    void offSale(Long skuId);

    SkuInfoDTO getSkuInfo(Long skuId);

    BigDecimal getSkuPrice(Long skuId);

    List<SpuSaleAttributeInfoDTO> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    List<PlatformAttributeInfoDTO> getPlatformAttrInfoBySku(Long skuId);
}
