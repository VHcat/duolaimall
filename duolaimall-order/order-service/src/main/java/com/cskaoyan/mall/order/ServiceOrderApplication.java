package com.cskaoyan.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 创建日期: 2023/03/15 11:44
 *
 * @author ciggar
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.cskaoyan.mall")
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan(basePackages = "com.cskaoyan.mall.order.mapper")
@EnableTransactionManagement
public class ServiceOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class,args);
    }
}
