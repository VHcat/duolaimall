package com.cskaoyan.mall.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class SpuInfoPageDTO {

    //"查询到的一页spu数据"
    private List<SpuInfoDTO> records;
    // 总页数
    //"满足条件的总的spu数量"
    private Integer total;
}
