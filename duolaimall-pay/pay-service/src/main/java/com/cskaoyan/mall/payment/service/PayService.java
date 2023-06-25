package com.cskaoyan.mall.payment.service;

import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.pay.api.dto.PaymentInfoDTO;
import com.cskaoyan.mall.payment.constant.PaymentStatus;

import java.util.Map;

public interface PayService {

    /**
     * 支付宝支付，获取支付表单
     */
    String createAliPay(Long orderId);

    /**
     * 保存支付记录
     */
    void savePaymentInfo(OrderInfoDTO orderInfo, String paymentTypeName);


    /**
     * 通过外部交易流水号和交易渠道 查询支付记录
     */
    PaymentInfoDTO queryPaymentInfoByOutTradeNoAndPaymentType(String outTradeNo, String payTypeName);

    /**
     * 修改支付记录表
     * 1. 更改状态
     * 2. 设置回调信息
     * 3. 假如出现异常，删除回调幂等标记
     */
    Boolean successPay(String outTradeNo, String name, Map<String, String> paramsMap);

    /**
     * 修改支付记录
     */
    void updatePaymentStatus(String outTradeNo, String name, PaymentStatus paymentStatus);
}
