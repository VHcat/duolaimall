package com.cskaoyan.mall.order.constant;

/**
 * 创建日期: 2023/03/15 11:23
 *
 * @author ciggar
 *
 * 订单类型
 */
public enum  OrderType {

    NORMAL_ORDER("普通订单"),
    PROMO_ORDER("秒杀订单");

    String desc;

    OrderType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}


