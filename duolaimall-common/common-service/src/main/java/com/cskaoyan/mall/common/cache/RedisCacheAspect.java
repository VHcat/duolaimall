package com.cskaoyan.mall.common.cache;

import com.cskaoyan.mall.common.constant.RedisConst;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


@Component
@Aspect
public class RedisCacheAspect {

    @Autowired
    private RedissonClient redissonClient;

    //  定义一个环绕通知！
    @SneakyThrows
    @Around("@annotation(com.cskaoyan.mall.common.cache.RedisCache)")
    public Object gmallCacheAspectMethod(ProceedingJoinPoint point) {
        //  定义一个对象
        Object obj = null;
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        RedisCache redisCache = methodSignature.getMethod().getAnnotation(RedisCache.class);
        //   获取到注解上的前缀
        String prefix = redisCache.prefix();
        //  组成缓存的key！ 获取方法传递的参数
        String key = prefix + Arrays.asList(point.getArgs()).toString();
        RLock lock = null;
        try {
            //  可以通过这个key 获取缓存的数据
            obj = this.redissonClient.getBucket(key).get();
            if (obj != null) {
                // 获取到了直接返回
                return obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //  加锁
            lock = redissonClient.getLock(key + ":lock");
            lock.lock(RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
            Object redisData = this.redissonClient.getBucket(key).get();
            // double check
            if (obj != null) {
                // 获取到了直接返回
                return obj;
            }

            //  执行业务逻辑：直接从数据库获取数据
            obj = point.proceed(point.getArgs());

            // 将结果放入redis
            obj = putInRedis(obj, key, methodSignature);

            return obj;

        } finally {
            //  解锁
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    /*
         将数据放入Redis缓存
     */
    private Object putInRedis(Object obj, String key, MethodSignature methodSignature) {
        try {
            if (obj == null) {
                //  防止缓存穿透
                //obj = new Object();
                Constructor declaredConstructor = methodSignature.getReturnType().getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                obj = declaredConstructor.newInstance();
                //  将缓存的数据变为 Json 的 字符串,默认值的过期时间是1分钟
                this.redissonClient.getBucket(key).set(obj, RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
            } else {
                //  将缓存的数据变为 Json 的 字符串
                this.redissonClient.getBucket(key).set(obj, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

}
