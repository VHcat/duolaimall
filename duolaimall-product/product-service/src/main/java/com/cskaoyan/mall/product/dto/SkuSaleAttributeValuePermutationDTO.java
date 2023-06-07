package com.cskaoyan.mall.product.dto;

import lombok.Data;

//"详情页中的sku商品的一种销售属性组合"
@Data
public class SkuSaleAttributeValuePermutationDTO {


    // "销售属性一种取值组合的拼接字符串"
    String valueIdsPermutationStr;

    // "sku商品id"
    Long skuId;
}
