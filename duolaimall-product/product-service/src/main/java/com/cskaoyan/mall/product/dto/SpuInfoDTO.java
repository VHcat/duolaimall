package com.cskaoyan.mall.product.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

@Data
public class SpuInfoDTO {

    //"spu商品id"
    private Long id;


    @TableField("spu_name")
    private String spuName;

    //"商品描述(后台简述）"
    private String description;

    //"三级分类id"
    private Long thirdLevelCategoryId;

    //"品牌id"
    private Long tmId;

    //"销售属性集合"
    private List<SpuSaleAttributeInfoDTO> spuSaleAttributeInfoList;

    //"商品的图片集合"
    private List<SpuImageDTO> SpuImageList;

    //"商品的海报图片集合"
    private List<SpuPosterDTO> SpuPosterList;
}
