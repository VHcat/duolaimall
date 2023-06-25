package com.cskaoyan.mall.ware.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.ware.service.WareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 创建日期: 2023/03/16 15:13
 *
 * @author ciggar
 */
@RestController
@RequestMapping("/api/ware")
public class WareApiController {

    @Autowired
    WareService wareService;

    /**
     * 订单服务调用，是否存在对应商品的库存
     */
    @GetMapping("/inner/hasStock/{skuId}/{num}")
    public Result hasStock(@PathVariable Long skuId, @PathVariable Integer num){

        Boolean ret = wareService.hasStock(skuId,num);
        if (ret) return Result.ok();
        return Result.fail();
    }


    /**
     * 支付成功回调：扣减库存
     */
    @PostMapping("/inner/decreaseStock/{orderId}")
    public Result decreaseStock(@PathVariable(value = "orderId") Long orderId){
        wareService.decreaseStock(orderId);
        return Result.ok();
    }

}
