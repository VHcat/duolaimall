package com.cskaoyan.mall.web.controller;

import com.alibaba.fastjson.JSON;
import com.cskaoyan.mall.order.dto.OrderTradeDTO;
import com.cskaoyan.mall.web.client.OrderApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 创建日期: 2023/03/15 14:54
 *
 * @author ciggar
 */
@Controller
@Slf4j
public class OrderController {

    @Autowired
    OrderApiClient orderApiClient;


    /**
     * 去结算
     * 获取订单结算页信息
     */
    @GetMapping("trade.html")
    public String trade(Model model, HttpServletRequest request) {
        log.info("enter {} for {}", OrderController.class.getSimpleName(), "trade");
        OrderTradeDTO orderTradeDTO = orderApiClient.getTradeInfo();
        System.out.println(JSON.toJSONString(orderTradeDTO));
        model.addAttribute("userAddressList",orderTradeDTO.getUserAddressList());
        model.addAttribute("detailArrayList",orderTradeDTO.getDetailArrayList());
        model.addAttribute("totalNum",orderTradeDTO.getTotalNum());
        model.addAttribute("totalAmount", orderTradeDTO.getTotalAmount());
        model.addAttribute("tradeNo",orderTradeDTO.getTradeNo());
        log.info("before render template {} for {}", "order/trade", "trade");
        return "order/trade";
    }



    /**
     * 我的订单：step1: 重定向
     */
    @GetMapping("myOrder.html")
    public String myOrder(){
        return "order/myOrder";
    }


}
