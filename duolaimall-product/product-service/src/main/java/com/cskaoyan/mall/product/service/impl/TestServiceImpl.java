package com.cskaoyan.mall.product.service.impl;

import com.cskaoyan.mall.product.service.TestService;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    RedissonClient redissonClient;

    @Override
    public void incrWithLock() {

         // 获取访问锁的桶
        RLock lock = redissonClient.getLock("lock:number");
        try {
            // 加锁
            lock.lock();
            // 加锁成功，代码执行到这里
            RBucket<Integer> bucket = redissonClient.getBucket("number");
            // 获取key为number的value值
            int number = bucket.get();
            // 自增1
            number++;
            // 在放回redis
            bucket.set(number);

        } finally {
            //  释放锁
            lock.unlock();
        }


    }
}
