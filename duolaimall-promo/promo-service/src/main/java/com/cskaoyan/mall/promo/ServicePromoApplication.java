package com.cskaoyan.mall.promo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 创建日期: 2023/03/19 15:16
 *
 * @author ciggar
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.cskaoyan.mall")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cskaoyan.mall")
@MapperScan(basePackages = "com.cskaoyan.mall.promo.mapper")
public class ServicePromoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicePromoApplication.class,args);
    }
}
