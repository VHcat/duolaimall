package com.cskaoyan.mall.search.param;

import lombok.Data;

// 封装查询条件
@Data
public class SearchParam {

    /*
        前端携带的请求参数：?thirdLevelCategoryId=61&trademark=2:华为&props=23:4G:运行内存&order=1:desc
        1. 三级类目：thirdLevelCategoryId=61
        2. 品牌参数(可能有多个)：trademark=2:华为 2是品牌id，华为是品牌名称
        3. 规格参数(平台属性，可能有多个):  props=23:4G:运行内存，23是属性id，4G是平台属性值，运行内存是属性名
        4. 排序参数：order=1:desc，1表示按照热度排序，2表示按照价格排序
     */

    private Long firstLevelCategoryId;
    private Long secondLevelCategoryId;
    private Long thirdLevelCategoryId;
    // trademark=2:华为
    private String trademark;//品牌

    private String keyword;//检索的关键字

    // 排序规则
    // 1:hotScore 2:price
    private String order = ""; // 1：综合排序/热度  2：价格

    // props=23:4G:运行内存
    //平台属性Id 平台属性值名称 平台属性名
    private String[] props;//页面提交的数组

    private Integer pageNo = 1;//分页信息
    private Integer pageSize = 3; // 每页默认显示的条数
}
