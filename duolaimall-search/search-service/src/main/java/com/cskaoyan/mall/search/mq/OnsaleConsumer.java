package com.cskaoyan.mall.search.mq;

import com.cskaoyan.mall.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by 北海 on 2023-06-19 17:12
 */
@Component
@Slf4j
public class OnsaleConsumer {

    @Value("${rocketmq.namesrv.addr}")
    String namesrvAddr;

    @Value("${rocketmq.consumer.group}")
    String consumerGroup;


    DefaultMQPushConsumer consumer;

    @Autowired
    SearchService searchService;

    @PostConstruct
    public void init() {
        consumer = new DefaultMQPushConsumer(consumerGroup + "_onsale");

        // 设置nameserver地址
        consumer.setNamesrvAddr(namesrvAddr);


        // 设置消息监听器
        consumer.setMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                // 获取消息
                MessageExt message = msgs.get(0);

                // 获取消息体中的数据
                byte[] body = message.getBody();

                String skuIdStr = new String(body, 0, body.length, Charset.forName("utf-8"));
                log.info("消费到消息准备上架 skuId={}", skuIdStr);
                try {
                    searchService.upperGoods(Long.parseLong(skuIdStr));
                    log.info("上架成功 skuId = {}", skuIdStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });


        String PRODUCT_ONSALE_TOPIC = "product_onsale_topic";
        try {
            // 订阅主题
            consumer.subscribe(PRODUCT_ONSALE_TOPIC, "*");

            // 启动消费者
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

    }



}
