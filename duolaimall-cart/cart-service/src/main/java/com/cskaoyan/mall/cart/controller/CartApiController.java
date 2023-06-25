package com.cskaoyan.mall.cart.controller;


import com.cskaoyan.mall.cart.api.dto.CartInfoDTO;
import com.cskaoyan.mall.cart.service.CartService;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.common.util.AuthContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/cart")
@SuppressWarnings("all")
public class CartApiController {

    @Autowired
    private CartService cartService;

    /**
     * @param skuId
     * @param skuNum
     * 功能描述: 添加商品到购物车
     */
    @RequestMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable Long skuId, @PathVariable Integer skuNum, HttpServletRequest request) {
        // 获取用户id
        String userId = AuthContext.getUserId(request);
        if (StringUtils.isBlank(userId)) {
            userId = AuthContext.getUserTempId(request);
        }
        cartService.addToCart(skuId,userId,skuNum);

        return Result.ok();
    }



    /**
     * @param request
     * @return
     */
    @GetMapping("cartList")
    public Result cartList(HttpServletRequest request) {
        // 获取用户Id
        String userId = AuthContext.getUserId(request);
        // 获取临时用户Id
        String userTempId = AuthContext.getUserTempId(request);

        List<CartInfoDTO> cartInfoList = cartService.getCartList(userId, userTempId);

        return Result.ok(cartInfoList);
    }



    /**
     * @param skuId      商品Id
     * @param isChecked  选中状态，1:选中  0:未选中
     * @param request
     * @return: com.cskaoyan.mall.common.result.Result
     * 功能描述:
     */
    @GetMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable Long skuId,
                            @PathVariable Integer isChecked,
                            HttpServletRequest request){

        String userId = AuthContext.getUserId(request);
        //  判断
        if (StringUtils.isEmpty(userId)){
            userId = AuthContext.getUserTempId(request);
        }
        cartService.checkCart(userId,isChecked,skuId);
        return Result.ok();
    }


    /**
     * 删除购物车指定的商品
     * @param skuId
     * @param request
     * @return
     */
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId, HttpServletRequest request) {
        // 如何获取userId
        String userId = AuthContext.getUserId(request);
        if (StringUtils.isEmpty(userId)) {
            // 获取临时用户Id
            userId = AuthContext.getUserTempId(request);
        }
        cartService.deleteCart(skuId, userId);
        return Result.ok();
    }


    /**
     * 删除购物车中所有选中的商品
     * 请求from：web-all#CartController.deleteChecked()
     */
    @DeleteMapping("deleteChecked")
    public Result deleteChecked(HttpServletRequest request) {
        String userId = AuthContext.getUserId(request);
        if (StringUtils.isBlank(userId)) {
            userId = AuthContext.getUserTempId(request);
        }
        cartService.deleteChecked(userId);
        return Result.ok();
    }



    // 订单服务调用
    // 下单的时候 查询购物车中所有被选中的商品
    @GetMapping("inner/getCartCheckedList/{userId}")
    public List<CartInfoDTO> getCartCheckedList(@PathVariable(value = "userId") String userId) {
        return cartService.getCartCheckedList(userId);
    }


    // 订单服务调用
    // 下单时候发现实时价格与购物车价格发生变动
    // 更新购物车价格
    @GetMapping("/inner/refresh/{userId}/{skuId}")
    public Result refreshCartPrice(@PathVariable String userId, @PathVariable Long skuId){
        cartService.refreshCartPrice(userId,skuId);
        return Result.ok();
    }


    // 订单服务调用
    // 下单之后删除购物车
    @PutMapping("/inner/delete/order/cart/{userId}")
    public Result removeCartProductsInOrder(@PathVariable("userId") String userId, @RequestBody List<Long> skuIds) {
        cartService.delete(userId, skuIds);
        return Result.ok();
    }
}
