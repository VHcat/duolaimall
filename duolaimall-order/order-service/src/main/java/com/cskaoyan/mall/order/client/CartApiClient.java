package com.cskaoyan.mall.order.client;


import com.cskaoyan.mall.cart.api.dto.CartInfoDTO;
import com.cskaoyan.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "service-cart")
public interface CartApiClient {

    // 下单的时候 查询购物车中所有被选中的商品
    @GetMapping("/api/cart/inner/getCartCheckedList/{userId}")
    public List<CartInfoDTO> getCartCheckedList(@PathVariable(value = "userId") String userId);

    // 更新用户购物车中商品价格
    @GetMapping("/api/cart/inner/refresh/{userId}/{skuId}")
    public Result refreshCartPrice(@PathVariable(value = "userId") String userId, @PathVariable(value = "skuId") Long skuId);

    @PutMapping("/api/cart/inner/delete/order/cart/{userId}")
    public Result removeCartProductsInOrder(@PathVariable("userId") String userId, @RequestBody List<Long> skuIds);
}
