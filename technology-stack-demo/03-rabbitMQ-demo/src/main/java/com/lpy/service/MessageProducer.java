package com.lpy.service;


import com.lpy.config.RabbitMQConfig;
import com.lpy.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送Direct消息
     */
    public void sendDirectMessage(String content) {
        Message message = new Message(UUID.randomUUID().toString(), content, "DIRECT");
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DIRECT_EXCHANGE,
                RabbitMQConfig.DIRECT_ROUTING_KEY,
                message
        );
        log.info("发送Direct消息: " + message);
    }

    /**
     * 发送Topic消息
     */
    public void sendTopicMessage(String routingKey, String content) {
        Message message = new Message(UUID.randomUUID().toString(), content, "TOPIC");
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOPIC_EXCHANGE,
                routingKey,
                message
        );
        log.info("发送Topic消息, RoutingKey: " + routingKey + ", Message: " + message);
    }

    /**
     * 发送Fanout消息
     */
    public void sendFanoutMessage(String content) {
        Message message = new Message(UUID.randomUUID().toString(), content, "FANOUT");
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.FANOUT_EXCHANGE,
                "",
                message
        );
        log.info("发送Fanout消息: " + message);
    }
}
