package com.lpy.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 消息生产者服务
 */
@Slf4j
@Service
public class MessageProducerService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送同步消息
     * @param topic 主题
     * @param message 消息内容
     * @return 发送结果
     */
    public SendResult sendSyncMessage(String topic, Object message) {
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(topic, message);
            log.info("同步发送消息成功 - Topic: {}, MessageId: {}, Status: {}",
                    topic, sendResult.getMsgId(), sendResult.getSendStatus());
            return sendResult;
        } catch (Exception e) {
            log.error("同步发送消息失败 - Topic: {}, Message: {}", topic, message, e);
            throw new RuntimeException("同步发送消息失败", e);
        }
    }

    /**
     * 发送异步消息
     * @param topic 主题
     * @param message 消息内容
     * @return CompletableFuture
     */
    public CompletableFuture<SendResult> sendAsyncMessage(String topic, Object message) {
        CompletableFuture<SendResult> future = new CompletableFuture<>();

        try {
            rocketMQTemplate.asyncSend(topic, message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("异步发送消息成功 - Topic: {}, MessageId: {}, Status: {}",
                            topic, sendResult.getMsgId(), sendResult.getSendStatus());
                    future.complete(sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("异步发送消息失败 - Topic: {}, Message: {}", topic, message, throwable);
                    future.completeExceptionally(throwable);
                }
            });
        } catch (Exception e) {
            log.error("异步发送消息异常 - Topic: {}, Message: {}", topic, message, e);
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * 发送单向消息（不关心发送结果）
     * @param topic 主题
     * @param message 消息内容
     */
    public void sendOneWayMessage(String topic, Object message) {
        try {
            rocketMQTemplate.sendOneWay(topic, message);
            log.info("单向发送消息完成 - Topic: {}", topic);
        } catch (Exception e) {
            log.error("单向发送消息失败 - Topic: {}, Message: {}", topic, message, e);
            throw new RuntimeException("单向发送消息失败", e);
        }
    }

    /**
     * 发送延时消息
     * @param topic 主题
     * @param message 消息内容
     * @param delayLevel 延时等级 (1-18，对应1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h)
     * @return 发送结果
     */
    public SendResult sendDelayMessage(String topic, Object message, int delayLevel) {
        try {
            Message<Object> msg = MessageBuilder.withPayload(message).build();
            SendResult sendResult = rocketMQTemplate.syncSend(topic, msg, 3000, delayLevel);
            log.info("延时消息发送成功 - Topic: {}, MessageId: {}, DelayLevel: {}",
                    topic, sendResult.getMsgId(), delayLevel);
            return sendResult;
        } catch (Exception e) {
            log.error("延时消息发送失败 - Topic: {}, DelayLevel: {}", topic, delayLevel, e);
            throw new RuntimeException("延时消息发送失败", e);
        }
    }

    /**
     * 发送带Tag的消息
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @return 发送结果
     */
    public SendResult sendMessageWithTag(String topic, String tag, Object message) {
        try {
            String destination = topic + ":" + tag;
            SendResult sendResult = rocketMQTemplate.syncSend(destination, message);
            log.info("带Tag消息发送成功 - Destination: {}, MessageId: {}",
                    destination, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.error("带Tag消息发送失败 - Topic: {}, Tag: {}", topic, tag, e);
            throw new RuntimeException("带Tag消息发送失败", e);
        }
    }

    /**
     * 发送顺序消息
     * @param topic 主题
     * @param message 消息内容
     * @param hashKey 分区键（相同hashKey的消息会发送到同一个队列，保证顺序）
     * @return 发送结果
     */
    public SendResult sendOrderlyMessage(String topic, Object message, String hashKey) {
        try {
            SendResult sendResult = rocketMQTemplate.syncSendOrderly(topic, message, hashKey);
            log.info("顺序消息发送成功 - Topic: {}, MessageId: {}, HashKey: {}",
                    topic, sendResult.getMsgId(), hashKey);
            return sendResult;
        } catch (Exception e) {
            log.error("顺序消息发送失败 - Topic: {}, HashKey: {}", topic, hashKey, e);
            throw new RuntimeException("顺序消息发送失败", e);
        }
    }

    /**
     * 发送事务消息
     * @param topic 主题
     * @param message 消息内容
     * @param arg 事务参数
     * @return 发送结果
     */
    public SendResult sendTransactionMessage(String topic, Object message, Object arg) {
        try {
            Message<Object> msg = MessageBuilder.withPayload(message).build();
            SendResult sendResult = rocketMQTemplate.sendMessageInTransaction(topic, msg, arg);
            log.info("事务消息发送完成 - Topic: {}, MessageId: {}, Status: {}",
                    topic, sendResult.getMsgId(), sendResult.getSendStatus());
            return sendResult;
        } catch (Exception e) {
            log.error("事务消息发送失败 - Topic: {}", topic, e);
            throw new RuntimeException("事务消息发送失败", e);
        }
    }

    /**
     * 发送自定义消息头的消息
     * @param topic 主题
     * @param message 消息内容
     * @param headers 消息头
     * @return 发送结果
     */
    public SendResult sendMessageWithHeaders(String topic, Object message,
                                             java.util.Map<String, Object> headers) {
        try {
            MessageBuilder<Object> builder = MessageBuilder.withPayload(message);
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(builder::setHeader);
            }
            Message<Object> msg = builder.build();

            SendResult sendResult = rocketMQTemplate.syncSend(topic, msg);
            log.info("自定义头消息发送成功 - Topic: {}, MessageId: {}",
                    topic, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.error("自定义头消息发送失败 - Topic: {}", topic, e);
            throw new RuntimeException("自定义头消息发送失败", e);
        }
    }
}