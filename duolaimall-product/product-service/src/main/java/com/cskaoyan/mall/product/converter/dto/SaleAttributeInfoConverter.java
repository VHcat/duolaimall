package com.cskaoyan.mall.product.converter.dto;

import com.cskaoyan.mall.product.dto.SaleAttributeInfoDTO;
import com.cskaoyan.mall.product.model.SaleAttributeInfo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleAttributeInfoConverter {

    SaleAttributeInfoDTO saleAttributeInfoPO2DTO(SaleAttributeInfo saleAttributeInfo);
    List<SaleAttributeInfoDTO> saleAttributeInfoPOs2DTOs(List<SaleAttributeInfo> saleAttributeInfos);
}
