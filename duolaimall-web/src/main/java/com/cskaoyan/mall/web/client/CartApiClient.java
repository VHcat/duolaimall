package com.cskaoyan.mall.web.client;

import com.cskaoyan.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;

/**
 * 创建日期: 2023/03/14 22:07
 *
 * @author ciggar
 */
@FeignClient(value = "service-cart")
public interface CartApiClient {

    /*
     * 功能描述: 删除用户购物车中已经选中的商品
     */
    @DeleteMapping("/api/cart/deleteChecked")
    Result deleteChecked();

}
