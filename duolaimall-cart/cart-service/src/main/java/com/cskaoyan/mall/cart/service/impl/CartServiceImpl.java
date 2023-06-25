package com.cskaoyan.mall.cart.service.impl;

import com.cskaoyan.mall.cart.api.dto.CartInfoDTO;
import com.cskaoyan.mall.cart.converter.SkuInfoConverter;
import com.cskaoyan.mall.cart.client.ProductApiClient;
import com.cskaoyan.mall.cart.service.CartService;
import com.cskaoyan.mall.common.constant.RedisConst;
import com.cskaoyan.mall.common.util.DateUtil;
import com.cskaoyan.mall.product.dto.SkuInfoDTO;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all")
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductApiClient productApiClient;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    SkuInfoConverter skuInfoConverter;

    @Override
    public void addToCart(Long skuId, String userId, Integer skuNum) {
        //  获取缓存key
        String cartKey = getCartKey(userId);

        Map<String, CartInfoDTO> cartInfoMap = this.redissonClient.getMap(cartKey);
        CartInfoDTO cartInfo = null;
        //包含的话更新数量
        if (cartInfoMap.containsKey(skuId.toString())) {
            cartInfo = cartInfoMap.get(skuId.toString());
            // 这里数量是每一次增减的数量
            cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
            cartInfo.setIsChecked(1);
            cartInfo.setSkuPrice(productApiClient.getSkuPrice(skuId));
            cartInfo.setUpdateTime(new Date());
        } else {
            //cartInfo = new CartInfo();
            //  给cartInfo 赋值！
            SkuInfoDTO skuInfo = productApiClient.getSkuInfo(skuId);
            cartInfo = skuInfoConverter.skuInfoToCartInfo(skuInfo, skuNum,skuId,userId);

        }
        cartInfoMap.put(skuId.toString(), cartInfo);

    }

    // 获取购物车的key
    private String getCartKey(String userId) {
        //定义key user:userId:cart
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }


    /**
     * 通过用户Id 查询购物车列表
     * 合并userId与userTempId的购物车、删除userTempId的购物车
     */
    @Override
    public List<CartInfoDTO> getCartList(String userId, String userTempId) {

        List<CartInfoDTO> noLoginCartInfoList = new ArrayList<>();
        // 1. 获取未登录的购物车列表
        noLoginCartInfoList = getCartInfoList(userTempId);

        // 2. 如果userId为空，那么表示未登录，直接返回未登录的 购物车列表
        if (StringUtils.isBlank(userId)) {

            noLoginCartInfoList.sort((o1, o2) -> {
                // 比较两个时间的大小，比较到秒
                return DateUtil.truncatedCompareTo(o2.getCreateTime(),o1.getCreateTime(), Calendar.SECOND);
            });
            return noLoginCartInfoList;
        }

        // 3. 如果userId不为空，表示已经登录
        // 4. 查询出已经登录的购物车
        List<CartInfoDTO> loginCartInfoList = new ArrayList<>();
        loginCartInfoList = getCartInfoList(userId);

        // 5. 合并
        List<CartInfoDTO> cartInfoList = mergeCartList(loginCartInfoList,noLoginCartInfoList);

        // 6. 排序
        cartInfoList.sort((o1, o2) -> {
            return DateUtil.truncatedCompareTo(o2.getCreateTime(),o1.getCreateTime(), Calendar.SECOND);
        });

        // 7. 更新存入Redis
        storeCartInfoList(cartInfoList,userId);

        // 8. 删除未登录的购物车
        deleteCart(userTempId);

        return cartInfoList;
    }


    /**
     * @param cartInfoList
     * @param userId
     * @return: void
     * 功能描述: 购物车更新，存入Redis
     */
    private void storeCartInfoList(List<CartInfoDTO> cartInfoList, String userId) {

        String cartKey = getCartKey(userId);
        RMap<String, CartInfoDTO> cartInfoRMap = redissonClient.getMap(cartKey);
        for (CartInfoDTO cartInfo : cartInfoList) {
            cartInfoRMap.put(cartInfo.getSkuId().toString(),cartInfo);
        }
    }

    /**
     * @param userId
     * @return: void
     * 功能描述: 删除对应用户的购物车
     */
    private void deleteCart(String userId) {

        String cartKey = getCartKey(userId);

        RMap<String, CartInfoDTO> cartMap = redissonClient.getMap(cartKey);

        // 删除购物车
        cartMap.delete();
    }

    /**
     * 功能描述: 购物车合并
     */
    private List<CartInfoDTO> mergeCartList(List<CartInfoDTO> loginCartInfoList, List<CartInfoDTO> noLoginCartInfoList) {

        List<CartInfoDTO> cartInfoList = new ArrayList<>();

        cartInfoList.addAll(loginCartInfoList);
        cartInfoList.addAll(noLoginCartInfoList);

        List<CartInfoDTO> newCartInfoList = cartInfoList.stream()
                .collect(Collectors.toMap(CartInfoDTO::getSkuId, a -> a, (o1, o2) -> {
                    o1.setSkuNum(o1.getSkuNum() + o2.getSkuNum());              // 处理数量
                    if (o1.getIsChecked() == 0 && o2.getIsChecked() == 0){      // 处理选中状态
                        o1.setIsChecked(0);
                    }else {
                        o1.setIsChecked(1);
                    }
                    return o1;
                })).values().stream().collect(Collectors.toList());
        return newCartInfoList;
    }


    /**
     * @param userId
     * 功能描述: 根据用户Id获取用户的购物车
     */
    private List<CartInfoDTO> getCartInfoList(String userId) {

        List<CartInfoDTO> cartInfoList = new ArrayList<>();

        String cartKey = getCartKey(userId);
        RMap<String, CartInfoDTO> cartMap = redissonClient.getMap(cartKey);
        if (cartMap.size() == 0) return cartInfoList;

        for (String skuId : cartMap.keySet()) {
            CartInfoDTO cartInfo = cartMap.get(skuId);
            cartInfoList.add(cartInfo);
        }
        return cartInfoList;
    }



    @Override
    public void checkCart(String userId, Integer isChecked, Long skuId) {
        String cartKey = this.getCartKey(userId);
        Map< String, CartInfoDTO> cartInfoRMap = this.redissonClient.getMap(cartKey);
        CartInfoDTO cartInfo = cartInfoRMap.get(skuId.toString());
        if(null != cartInfo) {
            cartInfo.setIsChecked(isChecked);
            cartInfoRMap.put(skuId.toString(), cartInfo);
        }
    }



    /**
     * @param skuId         商品id
     * @param userId        用户id
     * @return: void
     * 功能描述: 删除购物车指定的商品
     */
    @Override
    public void deleteCart(Long skuId, String userId) {
        Map<String, CartInfoDTO> cartInfoMap = redissonClient.getMap(this.getCartKey(userId));
        //  判断购物车中是否有该商品！
        if (cartInfoMap.containsKey(skuId.toString())){
            cartInfoMap.remove(skuId.toString());
        }
    }


    /**
     * @param userId
     * @return: void
     * 功能描述: 删除购物车中所有选中的商品
     */
    @Override
    public void deleteChecked(String userId) {
        String cartKey = getCartKey(userId);
        RMap<String, CartInfoDTO> cartMap = this.redissonClient.getMap(cartKey);
        for (String skuId : cartMap.keySet()) {
            CartInfoDTO cartInfo = cartMap.get(skuId);
            if (cartInfo.getIsChecked() == 1) {
                cartMap.remove(skuId);
            }
        }

    }






    @Override
    public List<CartInfoDTO> getCartCheckedList(String userId) {
        //  获取的选中的购物车列表！
        String cartKey = this.getCartKey(userId);
        //  获取到购物车集合数据：
        RMap<String, CartInfoDTO> cartInfoRMap = this.redissonClient.getMap(cartKey);
        List<CartInfoDTO>  cartInfoList = new ArrayList<>(cartInfoRMap.readAllValues());

        List<CartInfoDTO> checkedCartInfoList = cartInfoList.stream().filter(cartInfo -> {
            //  再次确认一下最新价格
            cartInfo.setSkuPrice(productApiClient.getSkuPrice(cartInfo.getSkuId()));
            return cartInfo.getIsChecked().intValue() == 1;
        }).collect(Collectors.toList());

        //  返回数据
        return checkedCartInfoList;
    }

    @Override
    public void delete(String userId, List<Long> skuIds) {
        Map< String, CartInfoDTO> catInfoMap = this.redissonClient.getMap(this.getCartKey(userId));

        skuIds.stream().forEach(skuId -> {
            //  判断购物车中是否有该商品！
            if (catInfoMap.containsKey(skuId.toString())){
                catInfoMap.remove(skuId.toString());
            }
        });
    }

    @Override
    public void refreshCartPrice(String userId, Long skuId) {

        // 获取用户购物车
        String cartKey = RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
        RMap<String, CartInfoDTO> cartMap = redissonClient.getMap(cartKey);

        CartInfoDTO cartInfo = cartMap.get(skuId);

        BigDecimal newPrice = productApiClient.getSkuPrice(skuId);
        cartInfo.setSkuPrice(newPrice);
        cartMap.put(skuId.toString(),cartInfo);
    }
}
