package com.cskaoyan.mall.order.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import com.cskaoyan.mall.order.constant.OrderType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
//订单信息
@TableName("order_info")
public class OrderInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

   //父订单编号
    @TableField("parent_order_id")
    private Long parentOrderId;

    //订单状态
    @TableField("order_status")
    private String orderStatus;

    //用户id
    @TableField("user_id")
    private Long userId;

    // 付款方式
    @TableField("payment_way")
    private String paymentWay = "ONLINE";

    //"收货人"
    @TableField("consignee")
    private String consignee;

    //"收件人电话"
    @TableField("consignee_tel")
    private String consigneeTel;

    //送货地址
    @TableField("delivery_address")
    private String deliveryAddress;

    //总金额
    @TableField("total_amount")
    private BigDecimal totalAmount;

    //原价金额
    @TableField("original_total_amount")
    private BigDecimal originalTotalAmount;

    //订单备注
    @TableField("order_comment")
    private String orderComment;

    //订单交易编号（第三方支付用)
    @TableField("out_trade_no")
    private String outTradeNo;

    //订单描述(第三方支付用)
    @TableField("trade_body")
    private String tradeBody;

    //订单类型（普通订单 | 秒杀订单）
    @TableField("order_type")
    private String orderType = OrderType.NORMAL_ORDER.name();


    //物流单编号
    @TableField("tracking_no")
    private String trackingNo;

    //可退款日期（签收后30天
    @TableField("refundable_time")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date refundableTime;


    //失效时间
    @TableField("expire_time")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date expireTime;


    @TableField(exist = false)
    private List<OrderDetail> orderDetailList;

    @TableField(exist = false)
    private String wareId;

    // 计算总价格
    public void sumTotalAmount(){
        BigDecimal totalAmount = new BigDecimal("0");
        //  计算最后
        for (OrderDetail orderDetail : orderDetailList) {
            BigDecimal skuTotalAmount = orderDetail.getOrderPrice().multiply(new BigDecimal(orderDetail.getSkuNum()));
            totalAmount = totalAmount.add(skuTotalAmount);
        }
        this.setTotalAmount(totalAmount);
        this.setOriginalTotalAmount(totalAmount);
    }
}
