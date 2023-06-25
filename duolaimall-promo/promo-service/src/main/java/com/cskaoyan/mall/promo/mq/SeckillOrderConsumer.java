package com.cskaoyan.mall.promo.mq;

import com.alibaba.fastjson.JSON;
import com.cskaoyan.mall.mq.constant.MqTopicConst;
import com.cskaoyan.mall.promo.model.UserRecord;
import com.cskaoyan.mall.promo.service.PromoService;
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
 * 创建日期: 2023/03/13 16:22
 *
 * @author ciggar
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class SeckillOrderConsumer {

    @Value("${rocketmq.namesrv.addr}")
    String namesrvAddr;

    @Value("${rocketmq.consumer.group}")
    String consumerGroup;

    DefaultMQPushConsumer mqComsumer;

    @Autowired
    PromoService promoService;


    @PostConstruct
    public void init() {
        // 初始化
        mqComsumer = new DefaultMQPushConsumer(consumerGroup + "-seckill");

        // 设置注册中心
        mqComsumer.setNamesrvAddr(namesrvAddr);

        // 订阅主题
        try {
            mqComsumer.subscribe(MqTopicConst.SECKILL_GOODS_QUEUE_TOPIC, "*");

            // 设置消息监听器
            mqComsumer.setMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                    // 获取秒杀排队消息
                    MessageExt message = list.get(0);
                    byte[] body = message.getBody();
                    String jsonStr = new String(body, 0, body.length, Charset.forName("utf-8"));
                    // 获取秒杀下单参数
                    UserRecord userRecord = JSON.parseObject(jsonStr, UserRecord.class);

                    // 调用
                    promoService.seckillOrder(userRecord.getUserId(), userRecord.getSkuId());


                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }

            });

            // 启动
            mqComsumer.start();
            log.info("SeckillConsumer 消息消费者启动成功! ");
        } catch (MQClientException e) {
            e.printStackTrace();
        }


    }
}
