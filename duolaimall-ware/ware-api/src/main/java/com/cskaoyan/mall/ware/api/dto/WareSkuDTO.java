package com.cskaoyan.mall.ware.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建日期: 2023/03/18 16:11
 *
 * @author ciggar
 */
@Data
public class WareSkuDTO {

    // 仓库id
    String wareId;

    // 商品id列表
    List<String> skuIds;

}
