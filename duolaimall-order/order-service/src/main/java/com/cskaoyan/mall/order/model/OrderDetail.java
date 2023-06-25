package com.cskaoyan.mall.order.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
//订单明细
@TableName("order_detail")
public class OrderDetail extends BaseEntity {
    private static final long serialVersionUID = 1L;

    //订单编号
    @TableField("order_id")
    private Long orderId;

    //sku_id
    @TableField("sku_id")
    private Long skuId;

    //sku名称（冗余)
    @TableField("sku_name")
    private String skuName;

    //图片名称
    @TableField("img_url")
    private String imgUrl;

    //购买价格(下单时sku价格)
    @TableField("order_price")
    private BigDecimal orderPrice;

    //购买个数
    @TableField("sku_num")
    private Integer skuNum;

    // 是否有足够的库存！
    @TableField(exist = false)
    private String hasStock;


}
