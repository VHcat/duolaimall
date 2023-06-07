package com.cskaoyan.mall.product.dto;

import lombok.Data;

@Data
public class SkuImageDTO {

    private Long id;

    //"商品id"
    private Long skuId;

    private String imgName;

    private String imgUrl;


    //"商品图片id")
    private Long spuImgId;

    //"是否默认"
    private String isDefault;
}
