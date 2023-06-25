package com.cskaoyan.mall.promo.service;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.promo.api.dto.SeckillGoodsDTO;

import java.util.List;

public interface PromoService {

    /**
     * 把秒杀商品列表信息导入Redis
     */
    void importIntoRedis();

    /**
     * 返回全部列表
     * @return
     */
    List<SeckillGoodsDTO> findAll();

    /**
     * 根据ID获取实体
     * @param skuId
     * @return
     */
    SeckillGoodsDTO getSeckillGoodsDTO(Long skuId);


    /***
     * 根据商品id与用户ID查看订单信息
     */
    Result checkOrder(Long skuId, String userId);

    /***
     * 秒杀下单
     */
    void seckillOrder(String userId, Long skuId);

    /**
     * 清理Redis缓存
     */
    void clearRedisCache();


}
