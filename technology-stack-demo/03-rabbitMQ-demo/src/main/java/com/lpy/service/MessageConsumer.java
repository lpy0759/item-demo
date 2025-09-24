package com.lpy.service;

import com.lpy.config.RabbitMQConfig;
import com.lpy.entity.Message;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class MessageConsumer {

    /**
     * 消费Direct消息
     */
    @RabbitListener(queues = RabbitMQConfig.DIRECT_QUEUE, containerFactory = "myFactory")
    public void receiveDirectMessage(@Payload Message message,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                     Channel channel) throws IOException {
        try {
            log.info("接收到Direct消息: " + message);
            // 模拟业务处理
            Thread.sleep(1000);
            // 手动确认
            log.info("deliveryTag : {}", deliveryTag);
            channel.basicAck(deliveryTag, false);
            log.info("Direct消息处理完成");
        } catch (Exception e) {
            log.error("处理Direct消息失败: " + e.getMessage());
            // 拒绝消息，不重新入队
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 消费Topic消息 - 队列1
     */
    @RabbitListener(queues = RabbitMQConfig.TOPIC_QUEUE_1)
    public void receiveTopicMessage1(@Payload Message message,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                     Channel channel) throws IOException {
        try {
            log.info("队列1接收到Topic消息: " + message);
            // 模拟业务处理
            Thread.sleep(500);
            // 手动确认
            channel.basicAck(deliveryTag, false);
            log.info("队列1 Topic消息处理完成");
        } catch (Exception e) {
            log.error("队列1处理Topic消息失败: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 消费Topic消息 - 队列2
     */
    @RabbitListener(queues = RabbitMQConfig.TOPIC_QUEUE_2)
    public void receiveTopicMessage2(@Payload Message message,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                     Channel channel) throws IOException {
        try {
            log.info("队列2接收到Topic消息: " + message);
            // 模拟业务处理
            Thread.sleep(500);
            // 手动确认
            channel.basicAck(deliveryTag, false);
            log.info("队列2 Topic消息处理完成");
        } catch (Exception e) {
            log.error("队列2处理Topic消息失败: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 消费Fanout消息 - 队列1
     */
    @RabbitListener(queues = RabbitMQConfig.FANOUT_QUEUE_1)
    public void receiveFanoutMessage1(@Payload Message message,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                      Channel channel) throws IOException {
        try {
            log.info("Fanout队列1接收到消息: " + message);
            // 模拟业务处理
            Thread.sleep(300);
            // 手动确认
            channel.basicAck(deliveryTag, false);
            log.info("Fanout队列1消息处理完成");
        } catch (Exception e) {
            log.error("Fanout队列1处理消息失败: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 消费Fanout消息 - 队列2
     */
    @RabbitListener(queues = RabbitMQConfig.FANOUT_QUEUE_2)
    public void receiveFanoutMessage2(@Payload Message message,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                      Channel channel) throws IOException {
        try {
            log.info("Fanout队列2接收到消息: " + message);
            // 模拟业务处理
            Thread.sleep(300);
            // 手动确认
            channel.basicAck(deliveryTag, false);
            log.info("Fanout队列2消息处理完成");
        } catch (Exception e) {
            log.error("Fanout队列2处理消息失败: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
