package com.lpy.service;

import com.lpy.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发送简单字符串消息
     *
     * @param topic 主题
     * @param message 消息内容
     */
    public void sendMessage(String topic, String message) {
        log.info("发送消息到主题 {}: {}", topic, message);

        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("消息发送成功: topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("消息发送失败: topic={}, message={}, error={}", topic, message, ex.getMessage());
            }
        });
    }

    /**
     * 发送带键的消息
     *
     * @param topic 主题
     * @param key 消息键
     * @param message 消息内容
     */
    public void sendMessage(String topic, String key, String message) {
        log.info("发送带键消息到主题 {} (key={}): {}", topic, key, message);

        kafkaTemplate.send(topic, key, message).addCallback(
                result -> log.info("消息发送成功: key={}, topic={}, partition={}, offset={}",
                        key, result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset()),
                failure -> log.error("消息发送失败: key={}, topic={}, error={}", key, topic, failure.getMessage())
        );
    }

    /**
     * 发送对象消息
     *
     * @param topic 主题
     * @param message 消息对象
     */
    public void sendMessage(String topic, Message message) {
        log.info("发送对象消息到主题 {}: {}", topic, message);

        kafkaTemplate.send(topic, message.getId(), message).addCallback(
                result -> log.info("对象消息发送成功: id={}, topic={}, partition={}, offset={}",
                        message.getId(), result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset()),
                failure -> log.error("对象消息发送失败: id={}, topic={}, error={}",
                        message.getId(), topic, failure.getMessage())
        );
    }

    /**
     * 批量发送消息
     *
     * @param topic 主题
     * @param messages 消息列表
     */
    public void sendBatchMessages(String topic, java.util.List<String> messages) {
        log.info("批量发送消息到主题 {}, 数量: {}", topic, messages.size());

        for (int i = 0; i < messages.size(); i++) {
            String message = messages.get(i);
            String key = "batch_" + i;
            sendMessage(topic, key, message);
        }
    }
}