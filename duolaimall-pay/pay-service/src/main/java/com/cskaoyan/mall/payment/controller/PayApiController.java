package com.cskaoyan.mall.payment.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.pay.api.dto.PaymentInfoDTO;
import com.cskaoyan.mall.payment.alipay.AlipayHelper;
import com.cskaoyan.mall.payment.constant.PaymentStatus;
import com.cskaoyan.mall.payment.constant.PaymentType;
import com.cskaoyan.mall.payment.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 创建日期: 2023/03/17 14:20
 *
 * @author ciggar
 */
@RestController
@RequestMapping("/api/payment/inner")
@Slf4j
public class PayApiController {

    @Autowired
    PayService payService;

    @Autowired
    AlipayHelper alipayHelper;


    /**
     * 订单超时取消：根据外部订单号 查询支付记录
     */
    @GetMapping("/getPaymentInfoByOutTradeNo/{outTradeNo}")
    public PaymentInfoDTO getPaymentInfoDTOByOutTradeNo(@PathVariable(value = "outTradeNo") String outTradeNo){
        return payService.queryPaymentInfoByOutTradeNoAndPaymentType(outTradeNo, PaymentType.ALIPAY.name());
    }

    /**
     * 订单超时取消：根据外部订单号 查询支付宝支付状态
     */
    @GetMapping("/getAlipayInfo/{outTradeNo}")
    public Result getAlipayInfo(@PathVariable(value = "outTradeNo") String outTradeNo){
        String tradeStatus = alipayHelper.queryAlipayTradeStatus(outTradeNo);
        if (StringUtils.isBlank(tradeStatus)){
            return Result.fail().message("查询支付宝支付状态失败！");
        }
        return Result.ok(tradeStatus);
    }

    /**
     * 订单超时取消：关闭支付宝支付记录
     */
    @GetMapping("/closeAlipay/{outTradeNo}")
    public Result closeAlipay(@PathVariable(value = "outTradeNo") String outTradeNo){
        alipayHelper.closeAlipay(outTradeNo);
        return Result.ok();
    }

    /**
     * 订单超时取消：修改paymentInfo为已关闭
     */
    @GetMapping("/closePaymentInfo/{outTradeNo}")
    public Result<Object> closePaymentInfo(@PathVariable(value = "outTradeNo") String outTradeNo){
        payService.updatePaymentStatus(outTradeNo, PaymentType.ALIPAY.name(), PaymentStatus.CLOSED);
        return Result.ok();
    }

}
