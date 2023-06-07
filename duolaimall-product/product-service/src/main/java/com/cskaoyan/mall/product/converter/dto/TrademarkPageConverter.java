package com.cskaoyan.mall.product.converter.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cskaoyan.mall.product.dto.TrademarkPageDTO;

import com.cskaoyan.mall.product.model.Trademark;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = TrademarkConverter.class)
public interface TrademarkPageConverter {

    TrademarkPageDTO tradeMarkPagePO2PageDTO(IPage<Trademark> trademarkPage);
}
