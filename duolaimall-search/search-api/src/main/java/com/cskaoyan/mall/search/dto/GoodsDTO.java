package com.cskaoyan.mall.search.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/*
     该类用来ES根据条件查询到的每一个SKU商品信息
 */
@Data
public class GoodsDTO {

    // 商品Id skuId
    private Long id;

    private String defaultImg;

    private String title;

    private Double price;

    private Date createTime; // 新品

    private Long tmId;


    private String tmName;

    private String tmLogoUrl;


    private Long firstLevelCategoryId;

    private String firstLevelCategoryName;

    private Long secondLevelCategoryId;


    private String secondLevelCategoryName;

    private Long thirdLevelCategoryId;


    private String thirdLevelCategoryName;

    //  商品的热度！ 我们将商品被用户点查看的次数越多，则说明热度就越高！

    private Long hotScore = 0L;

    // 平台属性集合对象
    // Nested 支持嵌套查询
    private List<SearchAttrDTO> attrs;
}
