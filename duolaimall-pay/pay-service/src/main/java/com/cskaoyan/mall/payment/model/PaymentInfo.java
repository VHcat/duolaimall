//
//
package com.cskaoyan.mall.payment.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * PaymentInfo
 * </p>
 *
 */
@Data
// 支付信息
@TableName("payment_info")
public class PaymentInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;

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

	// 交易编号
	@TableField("trade_no")
	private String tradeNo;

	//支付金额
	@TableField("total_amount")
	private BigDecimal totalAmount;

	// 交易内容
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

