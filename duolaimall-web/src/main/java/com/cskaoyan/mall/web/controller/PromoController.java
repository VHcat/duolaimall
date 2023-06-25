package com.cskaoyan.mall.web.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.order.dto.OrderTradeDTO;
import com.cskaoyan.mall.web.client.PromoApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 创建日期: 2023/03/19 16:32
 *
 * @author ciggar
 */
@Controller
@Slf4j
public class PromoController {

    @Autowired
    private PromoApiClient promoApiClient;


    /**
     * 秒杀列表
     * @param model
     * @return
     */
    @GetMapping("seckill.html")
    public String index(Model model) {
        log.info("enter {} for {}", PromoController.class.getSimpleName(), "index");
        Result result = promoApiClient.findAll();
        model.addAttribute("list", result.getData());
        log.info("before render template {} for {}", "seckill/index", "index");
        return "seckill/index";
    }

    /**
     * 功能描述: 秒杀商品详情
     */
    @GetMapping("seckill/{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model){
        log.info("enter {} for {}", PromoController.class.getSimpleName(), "getItem");
        // 通过skuId 查询skuInfo
        Result result = promoApiClient.getSeckillGoods(skuId);
        model.addAttribute("item", result.getData());
        log.info("before render template {} for {}", "seckill/item", "getItem");
        return "seckill/item";
    }

    /**
     * @param skuId 商品id
     * @param skuIdStr 下单码
     * @param request
     * @return: java.lang.String
     * 功能描述: 秒杀下单排队
     */
    @GetMapping("seckill/queue.html")
    public String queue(@RequestParam(name = "skuId") Long skuId,
                        @RequestParam(name = "skuIdStr") String skuIdStr,
                        HttpServletRequest request){
        log.info("enter {} for {}", PromoController.class.getSimpleName(), "queue");
        // 通过skuId 查询skuInfo
        request.setAttribute("skuId", skuId);
        request.setAttribute("skuIdStr", skuIdStr);
        log.info("before render template {} for {}", "seckill/queue", "queue");
        return "seckill/queue";
    }


    /**
     * 确认秒杀订单
     * @param model
     * @return
     */
    @GetMapping("seckill/trade.html")
    public String trade(Model model) {
        log.info("enter {} for {}", PromoController.class.getSimpleName(), "trade");
        Result<OrderTradeDTO> result = promoApiClient.trade();

        if(result.isOk()) {
            OrderTradeDTO orderTradeDTO = result.getData();
            model.addAttribute("userAddressList", orderTradeDTO.getUserAddressList());
            model.addAttribute("detailArrayList", orderTradeDTO.getDetailArrayList());
            model.addAttribute("totalAmount",orderTradeDTO.getTotalAmount());
            model.addAttribute("totalNum",orderTradeDTO.getTotalNum());
            log.info("before render template {} for {}", "seckill/trade", "trade");
            return "seckill/trade";
        } else {
            model.addAttribute("message",result.getMessage());
            log.info("before render template {} for {}", "seckill/fail", "trade");
            return "seckill/fail";
        }
    }

}
