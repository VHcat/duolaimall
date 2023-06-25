package com.cskaoyan.mall.pay.api.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 创建日期: 2023/03/17 16:55
 */
@Data
public class PaymentInfoDTO {

    Long id;

    //对外业务编号
    @TableField("out_trade_no")
    private String outTradeNo;

    //订单编号
    @TableField("order_id")
    private Long orderId;

    //用户Id
    @TableField("user_id")
    private Long userId;

    //支付类型（微信 支付宝）
    @TableField("payment_type")
    private String paymentType;

    //交易编号
    @TableField("trade_no")
    private String tradeNo;

    //支付金额
    @TableField("total_amount")
    private BigDecimal totalAmount;

    //交易内容
    @TableField("subject")
    private String subject;

    //支付状态
    @TableField("payment_status")
    private String paymentStatus;

    //创建时间
    @TableField("create_time")
    private Date createTime;

    //回调时间
    @TableField("callback_time")
    private Date callbackTime;

    //回调信息
    @TableField("callback_content")
    private String callbackContent;
}
