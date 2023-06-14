package com.cskaoyan.mall.web.vo;

import lombok.Data;

@Data
public class OrderVO {
    // 排序类型  1 热点  2价格
    String type;
    // asc or desc
    String sort;
}
