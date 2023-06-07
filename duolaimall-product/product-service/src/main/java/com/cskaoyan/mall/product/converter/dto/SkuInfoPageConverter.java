package com.cskaoyan.mall.product.converter.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.product.dto.SkuInfoPageDTO;
import com.cskaoyan.mall.product.model.SkuInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SkuInfoConverter.class)
public interface SkuInfoPageConverter {

    SkuInfoPageDTO skuInfoPagePO2PageDTO(Page<SkuInfo> skuInfoPage);

}
