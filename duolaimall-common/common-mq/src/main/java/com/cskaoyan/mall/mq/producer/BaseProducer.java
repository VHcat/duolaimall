package com.cskaoyan.mall.mq.producer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;

@Component
@Slf4j
public class BaseProducer {

    @Value("${rocketmq.namesrv.addr}")
    String namesrvAddr;

    @Value("${rocketmq.producer.group}")
    String producerGroup;

    DefaultMQProducer mqProducer;

    /*
         Spring容器，初始化会自动调用该方法
     */
    @PostConstruct
    public void init() {

        mqProducer = new DefaultMQProducer(producerGroup);

        mqProducer.setNamesrvAddr(namesrvAddr);

        try {
            mqProducer.start();
            log.info("mqProducer inited successed...namesrcAddr:{}, producerGroup:{}", namesrvAddr, producerGroup);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param topicName   主题名称
     * @param messageBody 消息内容
     * @return: java.lang.Boolean
     * 功能描述: 发送普通消息
     */
    public Boolean sendMessage(String topicName, Object messageBody) {

        try {
            String jsonMessage = JSON.toJSONString(messageBody);
            log.info("准备发送消息，topic:{}, message:{}", topicName, jsonMessage);

            Message message = new Message(topicName, jsonMessage.getBytes(Charset.forName("utf-8")));

            SendResult sendResult = mqProducer.send(message);

            if (sendResult == null || sendResult.getSendStatus() == null) return false;

            if (sendResult != null) {

                SendStatus sendStatus = sendResult.getSendStatus();
                if (sendStatus.equals(SendStatus.SEND_OK)) {
                    log.info("消息发送成功，topic:{}, message:{}", topicName, jsonMessage);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("消息发送异常，topic:{}, message:{}", topicName, messageBody);
        }
        return false;
    }

    /**
     * @param topicName   主题名称
     * @param messageBody 消息内容
     * @return: java.lang.Boolean
     * 功能描述: 发送延迟消息
     */
    public Boolean sendDelayMessage(String topicName, Object messageBody, int delayLevel) {

        try {
            String jsonMessage = JSON.toJSONString(messageBody);
            log.info("准备发送延迟消息，topic:{}, message:{}", topicName, jsonMessage);

            Message message = new Message(topicName, jsonMessage.getBytes(Charset.forName("utf-8")));

            // 设置消息延迟级别
            if (delayLevel <= 0) throw new RuntimeException("发送延迟消息，延迟级别应该大于0， 当前，delayLevel:" + delayLevel);

            message.setDelayTimeLevel(delayLevel);
            SendResult sendResult = mqProducer.send(message);

            if (sendResult == null || sendResult.getSendStatus() == null) return false;

            if (sendResult != null) {

                SendStatus sendStatus = sendResult.getSendStatus();
                if (sendStatus.equals(SendStatus.SEND_OK)) {
                    log.info("延迟消息发送成功，topic:{}, message:{}", topicName, jsonMessage);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("延迟消息发送异常，topic:{}, message:{}", topicName, messageBody);
        }
        return false;

    }

}
