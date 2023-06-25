package com.cskaoyan.mall.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 创建日期: 2023/03/17 11:33
 *
 * @author ciggar
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.cskaoyan.mall")
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan(basePackages = "com.cskaoyan.mall.payment.mapper")
public class ServicePayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicePayApplication.class,args);
    }
}
