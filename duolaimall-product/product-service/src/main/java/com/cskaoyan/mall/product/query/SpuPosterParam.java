package com.cskaoyan.mall.product.query;

import lombok.Data;

@Data
public class SpuPosterParam {

    private Long id;

    // "商品id"
    private Long spuId;

    // "文件名称"
    private String imgName;

    // "文件路径"
    private String imgUrl;
}
