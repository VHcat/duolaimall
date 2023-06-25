package com.cskaoyan.mall.promo.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("seckill_goods")
public class SeckillGoods extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("spu_id")
    private Long spuId;

    @TableField("sku_id")
    private Long skuId;

    @TableField("sku_name")
    private String skuName;

    @TableField("sku_default_img")
    private String skuDefaultImg;

    // 原价
    @TableField("price")
    private BigDecimal price;

    // 秒杀价格
    @TableField("cost_price")
    private BigDecimal costPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("create_time")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("check_time")
    private Date checkTime;

    // 审核状态
    @TableField("status")
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("start_time")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("end_time")
    private Date endTime;


    @TableField("num")
    private Integer num;


    @TableField("stock_count")
    private Integer stockCount;


    @TableField("sku_desc")
    private String skuDesc;

}

