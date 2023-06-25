package com.cskaoyan.mall.web.controller;

import com.cskaoyan.mall.product.dto.SkuInfoDTO;
import com.cskaoyan.mall.web.client.CartApiClient;
import com.cskaoyan.mall.web.client.ProductApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 创建日期: 2023/03/14 16:21
 */

@Controller
public class CartController {

    @Autowired
    ProductApiClient productApiClient;

    @Autowired
    CartApiClient cartApiClient;

    /**
     * 添加购物车
     * @param skuId
     * @param skuNum
     * @param request
     * @return
     */
    @RequestMapping("addCart.html")
    public String addCart(@RequestParam(name = "skuId") Long skuId,
                          @RequestParam(name = "skuNum") Integer skuNum,
                          HttpServletRequest request){
        SkuInfoDTO skuInfo = productApiClient.getSkuInfo(skuId);

        request.setAttribute("skuInfo",skuInfo);
        request.setAttribute("skuNum",skuNum);

        return "cart/addCart";
    }

    /**
     *
     * 用户点击“我的购物车”
     * 查看购物车
     * @param
     * @return
     */
    @RequestMapping("cart.html")
    public String index(){
        return "cart/index";
    }


    /**
     * 用户点击：“删除选中的商品”
     */
    @RequestMapping("cart/deleteChecked")
    public String deleteChecked(){
        cartApiClient.deleteChecked();
        return "cart/index";
    }


}
