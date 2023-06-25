package com.cskaoyan.mall.promo.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 创建日期: 2023/03/19 16:16
 *
 * @author ciggar
 */

@Data
public class SeckillGoodsDTO implements Serializable {

    private Long id;

    private Long spuId;

    private Long skuId;

    private String skuName;

    private String skuDefaultImg;

    private BigDecimal price;

    private BigDecimal costPrice;

    private Date checkTime;

    private String status;

    private Date startTime;

    private Date endTime;

    private Integer num;

    private Integer stockCount;

    private String skuDesc;
}
