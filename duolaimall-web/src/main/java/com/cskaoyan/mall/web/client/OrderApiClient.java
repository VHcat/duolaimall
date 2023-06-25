package com.cskaoyan.mall.web.client;

import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.order.dto.OrderTradeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-order")
public interface OrderApiClient {

    /**
     * 获取结算页信息
     */
    @GetMapping("/api/order/auth/trade")
    OrderTradeDTO getTradeInfo();

    /**
     * 下订单，根据Id获取订单信息
     */
    @GetMapping("/api/order/inner/getOrderInfo/{orderId}")
    public OrderInfoDTO getOrderInfoDTO(@PathVariable(value = "orderId") String orderId);



}
