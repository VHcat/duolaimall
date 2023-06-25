package com.cskaoyan.mall.web.client;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.order.dto.OrderTradeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 创建日期: 2023/03/19 16:32
 *
 * @author ciggar
 */
@FeignClient(value = "service-promo")
public interface PromoApiClient {

    /**
     * 功能描述: 获取所有的秒杀商品
     */
    @GetMapping("/api/promo/seckill/findAll")
    public Result findAll();

    /**
     * 功能描述: 获取秒杀商品详情
     */
    @GetMapping("/api/promo/seckill/getSeckillGoods/{skuId}")
    public Result getSeckillGoods(@PathVariable("skuId") Long skuId);



    /**
     * 秒杀确认订单
     * @return
     */
    @GetMapping("/api/promo/seckill/auth/trade")
    Result<OrderTradeDTO> trade();

}
