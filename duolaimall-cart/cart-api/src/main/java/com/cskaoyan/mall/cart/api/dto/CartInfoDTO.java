package com.cskaoyan.mall.cart.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 创建日期: 2023/03/16 09:48
 */
@Data
// 购物车
public class CartInfoDTO {
    private static final long serialVersionUID = 1L;

    //用户id
    private String userId;

    //skuid
    private Long skuId;

    //放入购物车时价格
    private BigDecimal cartPrice;

    //数量
    private Integer skuNum;

    //图片文件
    private String imgUrl;

    //sku名称
    private String skuName;

    //isChecked
    private Integer isChecked = 1;

    // 实时价格 skuInfo.price
    BigDecimal skuPrice;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}

