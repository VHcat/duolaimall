package com.cskaoyan.mall.common.constant;

/**
 * Redis常量配置类
 * set name admin
 */
public class RedisConst {

    public static final String SKUKEY_PREFIX = "sku:";
    public static final String SKUKEY_SUFFIX = ":info";
    //单位：秒
    public static final long SKUKEY_TIMEOUT = 24 * 60 * 60;
    // 定义变量，记录空对象的缓存过期时间
    public static final long SKUKEY_TEMPORARY_TIMEOUT = 5 * 60;

    //单位：秒 尝试获取锁的最大等待时间
    public static final long SKULOCK_EXPIRE_PX1 = 1;
    //单位：秒 锁的持有时间
    public static final long SKULOCK_EXPIRE_PX2 = 3;
    public static final String SKULOCK_SUFFIX = ":lock";

    public static final String USER_KEY_PREFIX = "user:";
    public static final String USER_CART_KEY_SUFFIX = ":cart";
    public static final long USER_CART_EXPIRE = 60 * 60 * 24 * 30;

    //用户登录
    public static final String USER_LOGIN_KEY_PREFIX = "user:login:";
    //    public static final String userinfoKey_suffix = ":info";
    public static final int USERKEY_TIMEOUT = 60 * 60 * 24 * 7;



    //  布隆过滤器使用！
    public static final String SKU_BLOOM_FILTER="sku:bloom:filter";

    // 订单
    // 用户订单交易流水号 + userId
    public static final String  ORDER_TRADE_CODE_PREFIX = "order:trade:code:";

    // 支付
    // 异步回调 验证notify_id
    public static final String PAY_CALL_BACK_VERFY_PREFIX = "pay:callback:notifyid:";
    // 异步回调 标记超时时间
    public static final int PAY_CALL_BACK_EXPIRE_TIME = 60 * 60;


    // 秒杀
    // 秒杀商品列表
    public static final String PROMO_SECKILL_GOODS = "promo:seckillgoods";

    // 秒杀商品库存
    public static final String PROMO_SECKILL_GOODS_STOCK_PREFIX = "promo:goods:stock:";

    // 秒杀商品库存状态位
    public static final String PROMO_SECKILL_CHANNEL_TOPIC = "promo:goods_channel_topic";

    // 用户秒杀下单标记，防止重复下单
    public static final String PROMO_USER_ORDERED_FLAG = "promo:user:orderflag:";

    public static final String PROMO_SUBMITTING = "promo:submitting";

    //用户下单标记锁定时间 单位：秒
    public static final int SECKILL__TIMEOUT = 60 * 60 * 1;

    // Redis中的 秒杀订单记录
    public static final String PROMO_SECKILL_ORDERS = "promo:orders";

    // 提交订单标记
    public static final String PROMO_SUBMIT_ORDER = "promo:submit";


}
