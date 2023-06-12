package com.cskaoyan.mall.product.converter.dto;

import com.cskaoyan.mall.product.dto.PlatformAttributeInfoDTO;
import com.cskaoyan.mall.product.dto.PlatformAttributeValueDTO;
import com.cskaoyan.mall.product.model.PlatformAttributeInfo;
import com.cskaoyan.mall.product.model.PlatformAttributeValue;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlatformAttributeInfoConverter {

    PlatformAttributeInfoDTO platformAttributeInfoPO2DTO(PlatformAttributeInfo platformAttributeInfo);

    List<PlatformAttributeInfoDTO> platformAttributeInfoPOs2DTOs(List<PlatformAttributeInfo> platformAttributeInfos);

    PlatformAttributeValueDTO platformAttributeValuePO2DTO(PlatformAttributeValue platformAttributeValue);

    List<PlatformAttributeValueDTO> platformAttributeValuePOs2DTOs(List<PlatformAttributeValue> platformAttributeValue);
}
