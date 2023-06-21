package com.cskaoyan.mall.search.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 总的数据
@Data
public class SearchResponseDTO implements Serializable {

    //品牌
    private List<SearchResponseTmDTO> trademarkList;
    //所有商品的顶头显示的筛选属性
    private List<SearchResponseAttrDTO> attrsList;

    //检索出来的商品信息
    private List<GoodsDTO> goodsList = new ArrayList<>();

    private Long total;//总记录数
    private Integer pageSize;//每页显示的内容
    private Integer pageNo;//当前页面
    private Long totalPages;

}
