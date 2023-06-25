package com.cskaoyan.mall.payment.client;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 创建日期: 2023/03/17 14:27
 *
 * @author ciggar
 */
@FeignClient(value = "service-order")
public interface OrderApiClient {

    // 通过订单id获取订单信息
    @GetMapping("/api/order/inner/getOrderInfo/{orderId}")
    public OrderInfoDTO getOrderInfoDTO(@PathVariable(value = "orderId") Long orderId);


    // 支付成功，修改订单状态
    @PostMapping("/api/order/inner/success/{orderId}")
    Result successPay(@PathVariable(value = "orderId") Long orderId);
}
