package com.cskaoyan.mall.search.mq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by 北海 on 2023-06-19 17:12
 */
@Component
public class OffSaleConsumer {


    @Value("${rocketmq.namesrv.addr}")
    String namesrvAddr;

    @Value("${rocketmq.consumer.group}")
    String consumerGroup;


    DefaultMQPushConsumer consumer;

    @PostConstruct
    public void init() {
        consumer = new DefaultMQPushConsumer(consumerGroup + "_offsale");

    }

}
