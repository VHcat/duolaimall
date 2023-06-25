package com.cskaoyan.mall.cart.service;



import com.cskaoyan.mall.cart.api.dto.CartInfoDTO;

import java.util.List;

public interface CartService {


    // 添加购物车 用户Id，商品Id，商品数量
    void addToCart(Long skuId, String userId, Integer skuNum);

    /**
     * 通过用户Id 查询购物车列表
     */
    List<CartInfoDTO> getCartList(String userId, String userTempId);

    /**
     * 更新选中状态
     */
    void checkCart(String userId, Integer isChecked, Long skuId);

    /**
     * @param skuId         商品id
     * @param userId        用户id
     * @return: void
     * 功能描述: 删除购物车指定的商品
     */
    void deleteCart(Long skuId, String userId);


    /**
     * @param userId
     * @return: void
     * 功能描述: 删除购物车中所有选中的商品
     */
    void deleteChecked(String userId);


    /**
     * 根据用户Id 查询购物车列表
     */
    List<CartInfoDTO> getCartCheckedList(String userId);

    /*
         根据skuId，删除购物车中的商品
     */
    void delete(String userId, List<Long> skuIds);

    /*
     更新用户购物车中商品的实时价格
    */
    void refreshCartPrice(String userId, Long skuId);
}
