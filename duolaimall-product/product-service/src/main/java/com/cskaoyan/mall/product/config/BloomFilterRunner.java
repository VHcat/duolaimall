package com.cskaoyan.mall.product.config;

import com.cskaoyan.mall.common.constant.RedisConst;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BloomFilterRunner implements CommandLineRunner {

    @Autowired
    RedissonClient redissonClient;

    @Override
    public void run(String... args) throws Exception {
        RBloomFilter<Long> rbloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
        // 初始化布隆过滤器，预计统计元素数量为100000，期望误差率为0.01
        rbloomFilter.tryInit(100000, 0.01);
    }
}
