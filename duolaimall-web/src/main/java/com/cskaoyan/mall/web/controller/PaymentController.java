package com.cskaoyan.mall.web.controller;

import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.web.client.OrderApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 创建日期: 2023/03/16 17:22
 */
@Controller
@Slf4j
public class PaymentController {

    @Autowired
    OrderApiClient orderApiClient;

    @GetMapping("pay.html")
    public String payIndex(String orderId, Model model){
        log.info("enter {} for {}", IndexController.class.getSimpleName(), "index");

        OrderInfoDTO orderInfoDTO = orderApiClient.getOrderInfoDTO(orderId);
        model.addAttribute("orderInfo",orderInfoDTO);
        log.info("before render template {} for {}", "payment/pay", "payIndex");
        return "payment/pay";
    }


    /**
     * 同步回调，重定向到支付成功页
     */
    @GetMapping("pay/success.html")
    public String success() {
        return "payment/success";
    }

}
