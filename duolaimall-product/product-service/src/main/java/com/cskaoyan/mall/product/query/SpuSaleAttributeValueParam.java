package com.cskaoyan.mall.product.query;

import lombok.Data;

@Data
public class SpuSaleAttributeValueParam {
    private Long id;

    //"spu销售属性id"
    private Long baseSaleAttrId;

    //"spu_id(冗余)"
    private Long spuId;

    // "spu销售属性值名称"
    private String saleAttrValueName;
}
