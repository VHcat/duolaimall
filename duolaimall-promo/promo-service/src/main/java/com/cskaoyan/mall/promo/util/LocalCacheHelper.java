package com.cskaoyan.mall.promo.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建日期: 2023/03/13 11:24
 *
 * @author ciggar
 *
 * 本地缓存类
 */
@Slf4j
public class LocalCacheHelper {

    /**
     * 缓存容器
     */
    private final static Map<String, Object> cacheMap = new ConcurrentHashMap<String, Object>();

    /**
     * 加入缓存
     *
     * @param key
     * @param cacheObject
     */
    public static void put(String key, Object cacheObject) {
        cacheMap.put(key, cacheObject);
        log.info("当前本地缓存 cacheMap:{}", JSON.toJSONString(cacheMap));
    }

    /**
     * 获取缓存
     * @param key
     * @return
     */
    public static Object get(String key) {
        return cacheMap.get(key);
    }

    /**
     * 清除缓存
     *
     * @param key
     * @return
     */
    public static void remove(String key) {
        cacheMap.remove(key);
    }


    public static void removeAll() {
        cacheMap.clear();
    }
}

