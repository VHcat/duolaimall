package com.cskaoyan.mall.product.converter.dto;

import com.cskaoyan.mall.product.dto.TestSkuProductDTO;
import com.cskaoyan.mall.product.model.SkuInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Created by 北海 on 2023-06-05 16:06
 */
@Mapper(componentModel = "spring")
public interface TestSkuProductConverter {

    // SkuInfo ——> TestSkuProductDTO
    @Mapping(source = "skuName", target = "productName")
    @Mapping(source = "skuDefaultImg", target = "url")
    TestSkuProductDTO skuInfo2TestSkuProductDTO(SkuInfo skuInfo);

}
