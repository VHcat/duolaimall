package com.cskaoyan.mall.order.client;

import com.cskaoyan.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 创建日期: 2023/03/16 12:01
 *
 * @author ciggar
 */
@FeignClient(value = "service-ware")
public interface WareApiClient {

    /**
     * 提交订单：检查是否有库存
     */
    @GetMapping("/api/ware/inner/hasStock/{skuId}/{num}")
    public Result hasStock(@PathVariable(value = "skuId") Long skuId, @PathVariable(value = "num") Integer num);


    /**
     * 支付成功回调：扣减库存
     */
    @PostMapping("/api/ware/inner/decreaseStock/{orderId}")
    Result decreaseStock(@PathVariable(value = "orderId") Long orderId);
}
