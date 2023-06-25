package com.cskaoyan.mall.order.query;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 创建日期: 2023/03/17 00:32
 *
 * @author ciggar
 */
@Data
public class OrderDetailParam {

    Long id;

    Long orderId;

    Long skuId;

    String skuName;

    String imgUrl;

    BigDecimal orderPrice;

    private Integer skuNum;

}
