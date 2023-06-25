package com.cskaoyan.mall.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 创建日期: 2023/03/15 15:26
 *
 * @author ciggar
 */
@Data
public class OrderDetailDTO {

    Long id;

    Long orderId;

    Long skuId;

    String skuName;

    String imgUrl;

    BigDecimal orderPrice;

    Integer skuNum;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    Date updateTime;
}
