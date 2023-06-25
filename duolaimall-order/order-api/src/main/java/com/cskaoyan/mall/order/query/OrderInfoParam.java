package com.cskaoyan.mall.order.query;

import lombok.Data;

import java.util.List;

/**
 * 创建日期: 2023/03/17 00:32
 *
 * @author ciggar
 */
@Data
public class OrderInfoParam {

    Long userId;

    String consignee;

    String consigneeTel;

    String deliveryAddress;

    String orderComment;

    String paymentWay = "ONLINE";

    List<OrderDetailParam> orderDetailList;
}
