package com.cskaoyan.mall.search.dto;

import lombok.Data;


@Data
public class SearchAttrDTO {

    // 平台属性Id
    private Long attrId;
    // 平台属性值名称
    private String attrValue;
    // 平台属性名
    private String attrName;
}
