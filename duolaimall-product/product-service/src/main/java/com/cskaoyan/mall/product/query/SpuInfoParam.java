package com.cskaoyan.mall.product.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.cskaoyan.mall.product.dto.SpuPosterDTO;
import lombok.Data;

import java.util.List;
@Data
public class SpuInfoParam {

    // "spu商品id"
    private Long id;


    @TableField("spu_name")
    private String spuName;

    // "商品描述(后台简述）"
    private String description;

    // "三级分类id"
    private Long category3Id;

    // "品牌id"
    private Long tmId;

    // "销售属性集合"
    private List<SpuSaleAttributeInfoParam> spuSaleAttrList;

    // "商品的图片集合"
    private List<SpuImageParam> SpuImageList;

    // "商品的海报图片集合"
    private List<SpuPosterParam> SpuPosterList;
}
