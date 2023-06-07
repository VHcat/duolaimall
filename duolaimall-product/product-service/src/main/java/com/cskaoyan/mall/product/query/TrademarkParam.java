package com.cskaoyan.mall.product.query;

import lombok.Data;

@Data
public class TrademarkParam {
    // "品牌id"
    private Long id;
    // "属性值"
    private String tmName;

    // "品牌logo的图片路径"
    private String logoUrl;
}
