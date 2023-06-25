package com.cskaoyan.mall.promo.task;

import com.cskaoyan.mall.promo.service.PromoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 创建日期: 2023/03/19 15:28
 *
 * @author ciggar
 */

@Component
@Slf4j
@EnableScheduling
public class ScheduledTask {


    @Autowired
    PromoService promoService;

    /**
     * 功能描述: 把秒杀商品导入Redis
     */
    //@Scheduled(cron = "0 0 1 * * ?")   // 每天1点执行
    @Scheduled(cron = "0 0/1 * * * ?")   // 每分钟执行一次
    public void importIntoRedisTask(){
        log.info("秒杀商品导入Redis 任务开始...");
        promoService.importIntoRedis();
        log.info("秒杀商品导入Redis 任务结束...");

    }


    //@Scheduled(cron = "0 0 18 * * ?")    // 每天18点执行   // 清除秒杀产生的Redis缓存
//    @Scheduled(cron = "0 0/1 * * * ?")   // 每分钟执行一次
    public void clearRedisCacheTask(){

        log.info("清理Redis缓存任务开始...");
         promoService.clearRedisCache();
        log.info("清理Redis缓存任务结束...");
    }






}
