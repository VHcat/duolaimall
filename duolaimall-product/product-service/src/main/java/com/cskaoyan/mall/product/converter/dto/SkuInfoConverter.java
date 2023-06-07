package com.cskaoyan.mall.product.converter.dto;

import com.cskaoyan.mall.product.dto.SkuImageDTO;
import com.cskaoyan.mall.product.dto.SkuInfoDTO;
import com.cskaoyan.mall.product.dto.SkuPlatformAttributeValueDTO;
import com.cskaoyan.mall.product.dto.SkuSaleAttributeValueDTO;
import com.cskaoyan.mall.product.model.SkuImage;
import com.cskaoyan.mall.product.model.SkuInfo;
import com.cskaoyan.mall.product.model.SkuPlatformAttributeValue;
import com.cskaoyan.mall.product.model.SkuSaleAttributeValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkuInfoConverter {

    SkuInfoDTO skuInfoPO2DTO(SkuInfo skuInfo);

    SkuImageDTO skuImagePO2DTO(SkuImage skuImage);

    SkuPlatformAttributeValueDTO skuPlatformAttributeValuePO2DTO(
            SkuPlatformAttributeValue skuPlatformAttributeValue);

    SkuSaleAttributeValueDTO skuSaleAttributeValuePOs2DTOs(
            SkuSaleAttributeValue skuSaleAttributeValue);
}
