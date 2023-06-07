package com.cskaoyan.mall.product.dto;

import lombok.Data;

@Data
public class SpuSaleAttributeValueDTO {

    private Long id;

    //"商品id"
    private Long spuId;

    //"销售属性id"
    private Long spuSaleAttrId;

    //"销售属性值名称"
    private String spuSaleAttrValueName;

    //"是否关联到了sku的属性值"
    String isChecked;
}
