package com.cskaoyan.mall.product.query;

import lombok.Data;

@Data
public class PlatformAttributeValueParam {

    //"平台属性值id"
    private Long id;

    //"属性值名称"
    private String valueName;

    //"属性id"
    private Long attrId;
}
