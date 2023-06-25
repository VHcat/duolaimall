package com.cskaoyan.mall.order.dto;

import com.cskaoyan.mall.order.constant.OrderType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 创建日期: 2023/03/16 17:25
 *
 * @author ciggar
 */
@Data
public class OrderInfoDTO {

    private Long id;

    //父订单编号
    private Long parentOrderId;

    //订单状态
    private String orderStatus;

    //用户id
    private Long userId;

    //付款方式
    private String paymentWay;

    //收货人
    private String consignee;

    //收件人电话
    private String consigneeTel;

    //送货地址
    private String deliveryAddress;

    //总金额
    private BigDecimal totalAmount;

    //原价金额
    private BigDecimal originalTotalAmount;

    //订单备注
    private String orderComment;

    //订单交易编号（第三方支付用)
    private String outTradeNo;

    //订单描述(第三方支付用)
    private String tradeBody;

    //订单类型（普通订单 | 秒杀订单）
    private String orderType = OrderType.NORMAL_ORDER.name();


    //物流单编号
    private String trackingNo;

    //可退款日期（签收后30天）
    private Date refundableTime;


    private Date createTime;

    private Date updateTime;

    //失效时间
    private Date expireTime;


    private List<OrderDetailDTO> orderDetailList;


    private String wareId;

    // 订单状态的名称
    private String orderStatusName;

    // 计算总价格
    public void sumTotalAmount(){
        BigDecimal totalAmount = new BigDecimal("0");
        //  计算最后
        for (OrderDetailDTO orderDetail : orderDetailList) {
            BigDecimal skuTotalAmount = orderDetail.getOrderPrice().multiply(new BigDecimal(orderDetail.getSkuNum()));
            totalAmount = totalAmount.add(skuTotalAmount);
        }
        this.setTotalAmount(totalAmount);
        this.setOriginalTotalAmount(totalAmount);
    }

}
