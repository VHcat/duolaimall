package com.cskaoyan.mall.search.converter;

import com.cskaoyan.mall.search.dto.GoodsDTO;
import com.cskaoyan.mall.search.dto.SearchAttrDTO;
import com.cskaoyan.mall.search.model.Goods;
import com.cskaoyan.mall.search.model.SearchAttr;
import org.mapstruct.Mapper;


import java.util.List;

@Mapper(componentModel = "spring")
public interface GoodsConverter {

    GoodsDTO goodsPO2DTO(Goods goods);

    List<GoodsDTO> goodsPOs2DTOs(List<Goods> goods);

    SearchAttrDTO searchAttrPO2DTO(SearchAttr searchAttr);
}
