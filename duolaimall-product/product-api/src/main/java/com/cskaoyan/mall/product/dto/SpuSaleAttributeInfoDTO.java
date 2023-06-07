package com.cskaoyan.mall.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class SpuSaleAttributeInfoDTO {

    private Long id;

    //"商品id"
    private Long spuId;

    //"销售属性id"
    private Long saleAttrId;

    //"销售属性名称(冗余)"
    private String saleAttrName;

    // 销售属性值对象集合
    List<SpuSaleAttributeValueDTO> spuSaleAttrValueList;
}
