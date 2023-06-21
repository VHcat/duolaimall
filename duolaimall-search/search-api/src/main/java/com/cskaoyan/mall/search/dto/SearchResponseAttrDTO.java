package com.cskaoyan.mall.search.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 平台属性相关对象
@Data
public class SearchResponseAttrDTO implements Serializable {

    // 平台属性Id
    private Long attrId;//1
    //当前属性值的集合
    private List<String> attrValueList = new ArrayList<>();
    //属性名称
    private String attrName;
}

