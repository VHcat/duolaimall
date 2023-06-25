package com.cskaoyan.mall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cskaoyan.mall.common.constant.RedisConst;
import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.pay.api.dto.PaymentInfoDTO;
import com.cskaoyan.mall.payment.alipay.AlipayHelper;
import com.cskaoyan.mall.payment.client.OrderApiClient;
import com.cskaoyan.mall.payment.constant.PaymentStatus;
import com.cskaoyan.mall.payment.constant.PaymentType;
import com.cskaoyan.mall.payment.converter.PaymentInfoConverter;
import com.cskaoyan.mall.payment.mapper.PaymentInfoMapper;
import com.cskaoyan.mall.payment.model.PaymentInfo;
import com.cskaoyan.mall.payment.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 创建日期: 2023/03/17 14:24
 */
@Service
@SuppressWarnings("all")
@Slf4j
public class PayServiceImpl implements PayService {

    @Autowired
    OrderApiClient orderApiClient;

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Autowired
    AlipayHelper alipayHelper;

    @Autowired
    PaymentInfoConverter paymentInfoConverter;

    @Autowired
    RedissonClient redissonClient;

    /**
     * 支付宝支付，获取支付表单
     */
    @Override
    public String createAliPay(Long orderId) {

        // 1. 校验支付订单，如果已完成或者是关闭，则返回字符串 "该订单已经完成或已关闭"
        // 调用订单服务
        // orderApiClient.getOrderInfoDTO

        OrderInfoDTO orderInfoDTO = orderApiClient.getOrderInfoDTO(orderId);
        if (PaymentStatus.PAID.name().equals(orderInfoDTO.getOrderStatus())
                || PaymentStatus.CLOSED.name().equals(orderInfoDTO.getOrderStatus())) {
            return "该订单已经完成或已关闭";
        }
        // 2. 保存支付记录 调用savePaymentInfo
        savePaymentInfo(orderInfoDTO, PaymentType.ALIPAY.name());


        // 3. 调用支付宝，生成表单 alipayHelper.createForm
        String form = alipayHelper.createForm(orderInfoDTO);
        // 4. 返回表单
        return form;
    }

    /**
     * 保存支付记录
     */
     public void savePaymentInfo(OrderInfoDTO orderInfo, String paymentTypeName) {

        // 先根据orderId 和 paymentType 查询，如果已经有支付记录，那么直接返回
         LambdaQueryWrapper<PaymentInfo> paymentInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
         paymentInfoLambdaQueryWrapper.eq(PaymentInfo::getOrderId, orderInfo.getId());
         paymentInfoLambdaQueryWrapper.eq(PaymentInfo::getPaymentType, paymentTypeName);

         Integer count = paymentInfoMapper.selectCount(paymentInfoLambdaQueryWrapper);
         if (count > 0) {
             // 如果已经有支付记录，那么直接返回
             return;
         }

         // 保存交易记录(设置createTime，paymentType，paymentStatus为未支付)
        PaymentInfo paymentInfo = paymentInfoConverter.contvertOrderInfoDTO2PaymentInfo(orderInfo);
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setPaymentType(paymentTypeName);
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());

        // 保存
         paymentInfoMapper.insert(paymentInfo);
    }


    /**
     * 通过外部交易流水号和交易渠道 查询支付记录
     */
    @Override
    public PaymentInfoDTO queryPaymentInfoByOutTradeNoAndPaymentType(String outTradeNo, String payTypeName) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("out_trade_no",outTradeNo);
        queryWrapper.eq("payment_type",payTypeName);

        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(queryWrapper);

        return paymentInfoConverter.convertPaymentInfoToDTO(paymentInfo);
    }


    /**
     * 修改支付记录表
     * 1. 更改状态
     * 2. 调用订单服务修改订单状态
     * 3. 假如出现异常，删除回调幂等标记
     */
    @Override
    public Boolean successPay(String outTradeNo, String payTypeName, Map<String, String> paramsMap) {
        try {
            // 根据 outTradeNo 和 payTypeName查询支付记录，如果没查询出来，则返回false
            LambdaQueryWrapper<PaymentInfo> paymentInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            paymentInfoLambdaQueryWrapper.eq(PaymentInfo::getOutTradeNo, outTradeNo);
            paymentInfoLambdaQueryWrapper.eq(PaymentInfo::getPaymentType, payTypeName);

            PaymentInfo paymentInfo = paymentInfoMapper.selectOne(paymentInfoLambdaQueryWrapper);
            paymentInfo.setUpdateTime(new Date());
            paymentInfo.setPaymentStatus(PaymentStatus.PAID.name());
            paymentInfo.setCallbackTime(new Date());
            paymentInfo.setCallbackContent(JSON.toJSONString(paramsMap));
            paymentInfo.setTradeNo(paramsMap.get("trade_no"));

            // 1. 修改支付表 记录的状态 PAID
            paymentInfoMapper.updateById(paymentInfo);

            // 2. 调用订单服务，修改订单状态
            orderApiClient.successPay(paymentInfo.getOrderId());

        } catch (Exception e) {
            e.printStackTrace();

            // 3. 删除回调notiryId标记
            redissonClient.getBucket(RedisConst.PAY_CALL_BACK_VERFY_PREFIX + paramsMap.get("notify_id"))
                    .delete();
        }
        return false;
    }

    /*
          根据outTradeNo 修改支付记录的状态
     */
    @Override
    public void updatePaymentStatus(String outTradeNo, String payTypeName, PaymentStatus paymentStatus) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus(paymentStatus.name());

        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no",outTradeNo);
        queryWrapper.eq("payment_type",payTypeName);

        paymentInfoMapper.update(paymentInfo,queryWrapper);
    }
}
