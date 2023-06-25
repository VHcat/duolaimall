package com.cskaoyan.mall.mq.constant;

public class MqTopicConst {


    // 商品上架
    public static String PRODUCT_ONSALE_TOPIC = "product_onsale_topic";

    // 商品下架
    public static String PRODUCT_OFFSALE_TOPIC = "product_offsale_topic";

    // 延迟取消订单Topic
    public static String DELAY_ORDER_TOPIC = "delay_order_topic";

    // 订单超时取消 延迟级别，延迟时间
    // 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 32m 1h 2h
    // 1  2  3   4   5  6  7  8  9  10 11 12 13 14  15  16  17 18
    public static Integer DELAY_ORDER_LEVEL = 16;

    // 支付回调，修改订单状态 Topic
    public static String PAY_CALL_BACK_UPDATE_ORDER = "PAY_CALL_BACK_UPDATE_ORDER_se";


    // 支付成功 库存扣减 topic
    public static String PAY_SUCCESS_WARE_STOCK_DEDUCK = "pay_success_ware_stock_deduck_se";


    // 库存服务，扣减库存成功，通知订单服务 修改订单状态 Topic
    public static String WARE_DEDUCK_STOCK_SUCCESS_TO_ORDER = "ware_deduck_stock_success_to_order_se";


    // 定时任务-把秒杀商品放入Redis
    public static String SCHEDULE_PROMO_PRODUCT_INTO_REDIS = "schedule_promo_product_into_redis";


    // 秒杀抢购队列
    public static String SECKILL_GOODS_QUEUE_TOPIC = "seckill_goods_queue_topic";


    // 秒杀产生的Redis缓存 清空Topic
    public static String SECKILL_REDIS_CACHE_CLEAR = "seckill_redis_cache_clear";


}
