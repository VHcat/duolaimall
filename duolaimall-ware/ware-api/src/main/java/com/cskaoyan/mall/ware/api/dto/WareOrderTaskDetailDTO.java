package com.cskaoyan.mall.ware.api.dto;

import lombok.Data;

/**
 * 创建日期: 2023/03/18 16:31
 *
 * @author ciggar
 */
@Data
public class WareOrderTaskDetailDTO {

    private String skuId;

    private String skuName;

    private Integer skuNum;

    private Long taskId;
}
