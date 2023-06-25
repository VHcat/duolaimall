package com.cskaoyan.mall.payment.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.cskaoyan.mall.common.constant.RedisConst;
import com.cskaoyan.mall.pay.api.dto.PaymentInfoDTO;
import com.cskaoyan.mall.payment.alipay.CsmallAlipayConfig;
import com.cskaoyan.mall.payment.constant.PaymentType;
import com.cskaoyan.mall.payment.service.PayService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 创建日期: 2023/03/18 22:47
 */
@Controller
@Slf4j
@RequestMapping("/api/payment/alipay")
public class AlipayController {

    @Autowired
    PayService payService;

    @Autowired
    CsmallAlipayConfig alipayConfig;

    @Autowired
    RedissonClient redissonClient;


    /**
     * 支付宝支付，获取支付表单
     */
    @RequestMapping("/submit/{orderId}")
    @ResponseBody
    public String submitOrder(@PathVariable Long orderId){
        String form = payService.createAliPay(orderId);
        return form;
    }

    /**
     * 支付成功：同步回调
     */
    @RequestMapping("/callback/return")
    public String callBack() {
        log.info("支付成功，同步回调！ ");
        // 同步回调给用户展示信息
        // redirect: 给浏览器发送重定向
        return "redirect:" + alipayConfig.getReturnOrderUrl();
    }

    /**
     * 支付成功：异步回调
     */
    @PostMapping("/callback/notify")
    @ResponseBody
    @SneakyThrows
    public String callbackNotify(@RequestParam Map<String, String> paramsMap){
        // 验签
        String checkNotifyResult = checkNotify(paramsMap);
        if (checkNotifyResult != null) return checkNotifyResult;
        // 执行业务逻辑
        String outTradeNo = paramsMap.get("out_trade_no");
        Boolean isSuccess = payService.successPay(outTradeNo, PaymentType.ALIPAY.name(), paramsMap);
        // 业务逻辑成功
        if (isSuccess) {
            return "success";
        }
        // 业务逻辑失败
        return "failure";
    }

    private String checkNotify(@RequestParam Map<String, String> paramsMap) throws AlipayApiException {
        boolean signVerified
                = AlipaySignature.rsaCheckV1(paramsMap, alipayConfig.getAlipayPublicKey(), CsmallAlipayConfig.charset, CsmallAlipayConfig.sign_type);
        if (!signVerified) {
            return "failure";
        }

        // 获取业务校验参数
        String out_trade_no = paramsMap.get("out_trade_no");
        String total_amount = paramsMap.get("total_amount");
        String app_id = paramsMap.get("app_id");
        String tradeStatus = paramsMap.get("trade_status");

        if (!alipayConfig.getAppId().equals(app_id)) {
            // 不是给我们的请求
            return "failure";
        }

        // 校验订单参数
        PaymentInfoDTO paymentInfoDTO
                = payService.queryPaymentInfoByOutTradeNoAndPaymentType(out_trade_no, PaymentType.ALIPAY.name());
        double result = Double.parseDouble(total_amount) - paymentInfoDTO.getTotalAmount().doubleValue();
        if (result != 0) {
            // 金额不对
            return "failure";
        }

        if (tradeStatus == null || (!"TRADE_SUCCESS".equals(tradeStatus) && (!"TRADE_FINISHED".equals(tradeStatus)))) {
            return "failure";
        }
        String notifyId = paramsMap.get("notify_id");
        // 幂等性校验
        RBucket<String> bucket
                = redissonClient.getBucket(RedisConst.PAY_CALL_BACK_VERFY_PREFIX + notifyId);
        // setnx
        boolean isSuccess = bucket.trySet(notifyId, 6 * 60 * 245, TimeUnit.SECONDS);
        if (!isSuccess) return "success";
        // 返回null表示校验通过
        return null;
    }

}
