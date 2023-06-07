package com.cskaoyan.mall.product.query;

import lombok.Data;

@Data
public class SkuImageParam {

    //"id"
    private Long id;

    //"商品id"
    private Long skuId;

    //"图片名称（冗余）"
    private String imgName;

    //"图片路径(冗余)"
    private String imgUrl;

    //"商品图片id"
    private Long spuImgId;

    //"是否默认"
    private String isDefault;
}
