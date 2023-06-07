package com.cskaoyan.mall.product.converter.dto;

import com.cskaoyan.mall.product.dto.TrademarkDTO;
import com.cskaoyan.mall.product.model.Trademark;
import com.cskaoyan.mall.product.query.TrademarkParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrademarkConverter {

    TrademarkDTO trademarkPO2DTO(Trademark trademark);

    List<TrademarkDTO> trademarkPOs2DTOs(List<Trademark> trademarks);

    Trademark trademarkParam2Trademark(TrademarkParam trademarkParam);

}
