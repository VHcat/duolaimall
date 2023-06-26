package com.cskaoyan.mall.promo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cskaoyan.mall.common.constant.RedisConst;
import com.cskaoyan.mall.common.constant.ResultCodeEnum;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.common.util.DateUtil;
import com.cskaoyan.mall.common.util.MD5;
import com.cskaoyan.mall.promo.api.dto.SeckillGoodsDTO;
import com.cskaoyan.mall.promo.constant.SeckillGoodsStatus;
import com.cskaoyan.mall.promo.converter.SeckillGoodsConverter;
import com.cskaoyan.mall.promo.mapper.SeckillGoodsMapper;
import com.cskaoyan.mall.promo.model.OrderRecord;
import com.cskaoyan.mall.promo.model.SeckillGoods;
import com.cskaoyan.mall.promo.service.PromoService;
import com.cskaoyan.mall.promo.util.LocalCacheHelper;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 创建日期: 2023/03/19 15:33
 *
 * @author ciggar
 */
@Service
@SuppressWarnings("all")
public class PromoServiceImpl implements PromoService {

    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SeckillGoodsConverter
            seckillGoodsConverter;

    /**
     * 把秒杀商品列表信息导入Redis
     */

    @Override
    public void importIntoRedis() {

        // 查询当天应该参与秒杀活动的秒杀商品
        // 1. 状态  CHECKED_PASS
        // 2. 必须是当天的日起
        // 3. stock_count > 0

        QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "CHECKED_PASS");
        queryWrapper.eq("DATE_FORMAT(start_time,'%Y-%m-%d')", DateUtil.formatDate(new Date()));
        queryWrapper.gt("stock_count", 0);

        // 参与当天秒杀活动的秒杀商品
        List<SeckillGoods> goods = seckillGoodsMapper.selectList(queryWrapper);

        goods.forEach(seckillGoods -> {

            // 将秒杀商品存储到Redis
            RMap<String, SeckillGoods> goodsRMap = redissonClient.getMap(RedisConst.PROMO_SECKILL_GOODS);
            if (!goodsRMap.containsKey(seckillGoods.getSkuId().toString())
                    || LocalCacheHelper.get(seckillGoods.getSkuId().toString()) == null) {
                goodsRMap.put(seckillGoods.getSkuId().toString(), seckillGoods);

                // 初始化该商品的库存队列
                RDeque<Long> stockQueue = redissonClient.getDeque(RedisConst.PROMO_SECKILL_GOODS_STOCK_PREFIX
                        + seckillGoods.getSkuId());

                for (int i = 0; i < seckillGoods.getStockCount(); i++) {
                    stockQueue.push(seckillGoods.getSkuId());
                }

                // 初始化秒杀商品的库存状态位
                LocalCacheHelper.put(seckillGoods.getSkuId().toString(), "1");
            }

        });


    }

    /**
     * 返回全部列表
     *
     * @return
     */
    @Override
    public List<SeckillGoodsDTO> findAll() {
        // 获取所有的秒杀商品存储的hash数据结构的值
        RMap<String, SeckillGoods> map = redissonClient.getMap(RedisConst.PROMO_SECKILL_GOODS);
        // 获取所有的秒杀商品
        Collection<SeckillGoods> values = map.values();

        List<SeckillGoods> seckillGoods = new ArrayList<>(values);

        List<SeckillGoodsDTO> seckillGoodsDTOS =
                seckillGoodsConverter.convertSeckillGoodsList(seckillGoods);
        return seckillGoodsDTOS;

    }


    /**
     * 根据ID获取实体
     *
     * @param skuId
     * @return
     */
    @Override
    public SeckillGoodsDTO getSeckillGoodsDTO(Long skuId) {

        RMap<String, SeckillGoods> map = redissonClient.getMap(RedisConst.PROMO_SECKILL_GOODS);

        SeckillGoods seckillGoods = map.get(skuId.toString());

        SeckillGoodsDTO seckillGoodsDTO = seckillGoodsConverter.convertSeckillGoodsToDTO(seckillGoods);
        return seckillGoodsDTO;
    }


    @Override
    public Result checkOrder(Long skuId, String userId) {

        // 1. 判断一下用户是否已经提交过秒杀下单排队请求
        RBucket<Long> bucket = redissonClient.getBucket(RedisConst.PROMO_USER_ORDERED_FLAG + userId);
        if (bucket.get() == null)  {
            return Result.build(null, ResultCodeEnum.SECKILL_RUN);
        }

        RMap<String, OrderRecord> orderRecordRMap = redissonClient.getMap(RedisConst.PROMO_SECKILL_ORDERS);
        if (orderRecordRMap.get(userId.toString()) != null) {
           // 已经成功获取秒杀下单资格
           return Result.build(null, ResultCodeEnum.SECKILL_SUCCESS);
        }

        // 获取下单信息(提交订单)
        RMap<String, String> map = redissonClient.getMap(RedisConst.PROMO_SUBMIT_ORDER);
        String orderId = map.get(userId);
        if (orderId != null) {
            return Result.build(null, ResultCodeEnum.SECKILL_ORDER_SUCCESS);
        }

        // 判断秒杀商品状态为
        String flag = (String) LocalCacheHelper.get(skuId.toString());
        if ("0".equals(flag)) {
            // 秒杀商品售罄
            return Result.build(null, ResultCodeEnum.SECKILL_FINISH);
        }

        // 否则，返回排队中
        return Result.build(null, ResultCodeEnum.SECKILL_RUN);
    }

    @Override
    public void seckillOrder(String userId, Long skuId) {

        // 判断库存是否已经售罄
        String flag = (String) LocalCacheHelper.get(skuId.toString());
        if ("0".equals(flag)) return;

        // 给用户的秒杀下单打上一个标志
        RBucket<Long> bucket = redissonClient.getBucket(RedisConst.PROMO_USER_ORDERED_FLAG + userId);

        // 计算标记的过期时间
        SeckillGoods goods = getSeckillGoods(skuId);
        Date endTime = goods.getEndTime();
        long timeOut = endTime.getTime() - new Date().getTime();
        long timeOutSecond = timeOut / 1000;

        // setnx
        boolean isFirst = bucket.trySet(Long.parseLong(userId), timeOutSecond, TimeUnit.SECONDS);
        if (!isFirst) {
            return;
        }

        //  扣减秒杀商品库存
        RDeque<Long> stockQueue
                = redissonClient.getDeque(RedisConst.PROMO_SECKILL_GOODS_STOCK_PREFIX + skuId);

        if (stockQueue.poll() == null) {
            // 修改秒杀商品的状态位
            LocalCacheHelper.put(skuId.toString(), "0");
            return;
        }

        // 已经真正获取到了秒杀下单的资格， 将带下单的信息，暂存在Redis中
        OrderRecord orderRecord = new OrderRecord();
        orderRecord.setUserId(userId);
        orderRecord.setNum(1);
        orderRecord.setOrderStr(MD5.encrypt(userId + skuId));

        SeckillGoods seckillGoods = getSeckillGoods(skuId);
        orderRecord.setSeckillGoods(seckillGoods);

        RMap<String, OrderRecord> orderRecordRMap = redissonClient.getMap(RedisConst.PROMO_SECKILL_ORDERS);
        orderRecordRMap.put(userId,orderRecord);

        // 更新库存
        updateStock(skuId, userId);
    }

    private void updateStock(Long skuId, String userId) {

        RLock lock =
                redissonClient.getLock(RedisConst.PROMO_SECKILL_GOODS_STOCK_PREFIX
                        + skuId + ":lock");

        lock.lock();
        try {
            // 先获取当前的秒杀库存
            RDeque<Long> deque =
                    redissonClient.getDeque(RedisConst.PROMO_SECKILL_GOODS_STOCK_PREFIX + skuId);
            int count = deque.size();

            SeckillGoods seckillGoods = getSeckillGoods(skuId);
            seckillGoods.setStockCount(count);
            // 更新数据库中的库存数量
            seckillGoodsMapper.updateById(seckillGoods);

            // 更新秒杀商品的库存字段
            RMap<String, SeckillGoods> map = redissonClient.getMap(RedisConst.PROMO_SECKILL_GOODS);
            map.put(skuId.toString(), seckillGoods);

        }finally {
            if (lock != null) {
                lock.unlock();
            }
        }

    }


    /**
     * 清理Redis缓存
     */
    @Override
    public void clearRedisCache() {

        // 1.  查询数据库中参与当天秒杀活动的所有秒杀商品
        QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "CHECKED_PASS");
        queryWrapper.eq("DATE_FORMAT(start_time, '%Y-%m-%d')",DateUtil.formatDate(new Date()));

        List<SeckillGoods> goods = seckillGoodsMapper.selectList(queryWrapper);

        goods.forEach( seckillGoods -> {

            // 清理库存队列
            redissonClient.getDeque(RedisConst.PROMO_SECKILL_GOODS_STOCK_PREFIX
                    + seckillGoods.getSkuId()).delete();

            // 修改秒杀商品的状态为 FINISH
            seckillGoods.setStatus(SeckillGoodsStatus.FINISHED.name());
            // 保存到数据库
            seckillGoodsMapper.updateById(seckillGoods);

        });

        // 删除当天的秒杀商品的缓存数据
        redissonClient.getMap(RedisConst.PROMO_SECKILL_GOODS).delete();

        // 删除暂存的带下单信息的那个map
        redissonClient.getMap(RedisConst.PROMO_SECKILL_ORDERS).delete();

        // 删除记录用户提交订单信息的map
        redissonClient.getMap(RedisConst.PROMO_SUBMIT_ORDER).delete();

        // 删除用户的下单标记
        redissonClient.getMap(RedisConst.PROMO_SUBMITTING).delete();

        // 库存状态标志位
        LocalCacheHelper.removeAll();
    }


    /**
     * 根据SkuId获取秒杀商品
     */
    private SeckillGoods getSeckillGoods(Long skuId) {
        RMap<Long, SeckillGoods> map = redissonClient.getMap(RedisConst.PROMO_SECKILL_GOODS);
        return map.get(skuId.toString());
    }

}
