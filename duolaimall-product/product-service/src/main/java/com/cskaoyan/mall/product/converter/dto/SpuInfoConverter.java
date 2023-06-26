package com.cskaoyan.mall.product.converter.dto;

import com.cskaoyan.mall.product.dto.*;
import com.cskaoyan.mall.product.model.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpuInfoConverter {

    SpuInfoDTO spuInfoPO2DTO(SpuInfo spuInfo);

    SpuSaleAttributeInfoDTO spuSaleAttributeInfoPO2DTO(SpuSaleAttributeInfo spuSaleAttributeInfo);
    List<SpuSaleAttributeInfoDTO> spuSaleAttributeInfoPOs2DTOs(List<SpuSaleAttributeInfo> spuSaleAttributeInfos);

    SpuSaleAttributeValueDTO spuSaleAttributeValuePO2DTO (SpuSaleAttributeValue spuSaleAttributeValue);
    List<SpuSaleAttributeValueDTO> spuSaleAttributeValuePOs2DTOs (List<SpuSaleAttributeValue> spuSaleAttributeValues);

    SpuImageDTO spuImagePO2spuImageDTO(SpuImage spuImage);
    List<SpuImageDTO> spuImagePOs2DTOs(List<SpuImage> spuImages);

    SpuPosterDTO spuPosterPO2DTO(SpuPoster spuPoster);
    List<SpuPosterDTO> spuPosterPOs2DTOs(List<SpuPoster> spuPosters);

}
