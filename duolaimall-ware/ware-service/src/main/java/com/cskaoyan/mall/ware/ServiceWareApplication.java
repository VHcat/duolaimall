package com.cskaoyan.mall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 创建日期: 2023/03/16 14:40
 *
 * @author ciggar
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.cskaoyan.mall")
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan(basePackages = "com.cskaoyan.mall.ware.mapper")
public class ServiceWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceWareApplication.class,args);
    }
}
