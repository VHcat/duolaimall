package com.cskaoyan.mall.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class SkuInfoPageDTO {

    //"查询到的一页spu数据"
    private List<SkuInfoDTO> records;
    // 总页数
    //"满足条件的总的spu数量"
    private Integer total;
}
