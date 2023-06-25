package com.cskaoyan.mall.order.mq;

import com.cskaoyan.mall.mq.constant.MqTopicConst;
import com.cskaoyan.mall.order.service.OrderService;
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
 * Created by 北海 on 2023-05-05 11:12
 */
@Component
public class DelayOrderCancelConsumer {

    @Value("${rocketmq.consumer.group}")
    private String consumerGroup;

    @Value("${rocketmq.namesrv.addr}")
    String namesrvAddr;


    private DefaultMQPushConsumer consumer;

    @Autowired
    OrderService orderService;

    @PostConstruct
    public void init() {
        // 创建consumer对象
        consumer = new DefaultMQPushConsumer(consumerGroup + "delay_order_consumer");

        // 设置nameserver地址
        consumer.setNamesrvAddr(namesrvAddr);

        // 设置消息监听器
        consumer.setMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                // 获取延迟消息，从延迟消息的消息体中，取出orderId
                MessageExt messageExt = msgs.get(0);

                byte[] body = messageExt.getBody();

                String orderId = new String(body, 0, body.length, Charset.forName("utf-8"));
                //
                try {
                    orderService.execExpiredOrder(Long.parseLong(orderId));
                } catch (Exception e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        try {
            // 订阅主题
            consumer.subscribe(MqTopicConst.DELAY_ORDER_TOPIC, "*");

            // 启动消费者
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

    }

}
