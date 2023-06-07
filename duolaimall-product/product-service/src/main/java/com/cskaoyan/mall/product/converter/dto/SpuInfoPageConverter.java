package com.cskaoyan.mall.product.converter.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.product.dto.SpuInfoPageDTO;
import com.cskaoyan.mall.product.model.SpuInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SpuInfoConverter.class)
public interface SpuInfoPageConverter {

    SpuInfoPageDTO spuInfoPage2PageDTO(Page<SpuInfo> SpuInfoPage);

}
