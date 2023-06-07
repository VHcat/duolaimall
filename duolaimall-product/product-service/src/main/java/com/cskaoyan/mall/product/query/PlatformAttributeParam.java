package com.cskaoyan.mall.product.query;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

@Data
public class PlatformAttributeParam {

    // "平台属性id"
    private Long id;

    //"属性名称"
    private String attrName;

    //"分类id"
    @TableField("category_id")
    private Long categoryId;

    //"分类层级"
    private Integer categoryLevel;

    /*
        平台属性值集合，这里注意一个平台属性，有多个属性取值
     */
    //"平台属性值列表"
    private List<PlatformAttributeValueParam> attrValueList;
}
