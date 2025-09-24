package com.lpy.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpy.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class KafkaConsumerService {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 监听简单字符串消息
     */
    @KafkaListener(topics = "test-topic", groupId = "demo-group-1")
    public void consumeMessage(String message) {
        log.info("接收到消息: {}", message);
        // 处理消息逻辑
        processMessage(message);
    }

    /**
     * 监听带详细信息的消息
     */
    @KafkaListener(topics = "test-detail-topic", groupId = "demo-group-2")
    public void consumeMessageWithDetails(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {

        log.info("接收到详细消息: topic={}, partition={}, offset={}, key={}, timestamp={}, message={}",
                topic, partition, offset, key, timestamp, message);
        processMessage(message);
    }

    /**
     * 监听对象消息
     */
    @KafkaListener(topics = "object-topic", groupId = "demo-group-3")
    public void consumeObjectMessage(Message message) {
        log.info("接收到对象消息: {}", message);
        // 处理对象消息
        processObjectMessage(message);
    }

    /**
     * 监听消息并手动确认
     */
    @KafkaListener(topics = "manual-ack-topic", groupId = "demo-group-4")
    public void consumeMessageWithManualAck(
            ConsumerRecord<String, String> record,
            Acknowledgment ack) {

        try {
            log.info("接收到需手动确认的消息: key={}, value={}, partition={}, offset={}",
                    record.key(), record.value(), record.partition(), record.offset());

            // 处理消息
            processMessage(record.value());

            // 手动确认
            ack.acknowledge();
            log.info("消息处理完成并已确认");

        } catch (Exception e) {
            log.error("处理消息失败: {}", e.getMessage());
            // 根据业务需求决定是否确认
            // ack.acknowledge(); // 确认消息，避免重复消费
        }
    }

    /**
     * 批量消息监听
     */
    @KafkaListener(topics = "batch-topic", groupId = "demo-group-5", containerFactory = "batchKafkaListenerContainerFactory")
    public void consumeBatchMessages(List<ConsumerRecord<String, String>> records) {
        log.info("接收到批量消息，数量: {}", records.size());

        for (ConsumerRecord<String, String> record : records) {
            log.info("批量消息处理: key={}, value={}, partition={}, offset={}",
                    record.key(), record.value(), record.partition(), record.offset());
            processMessage(record.value());
        }
    }

    /**
     * 指定分区监听
     */
    @KafkaListener(
            topicPartitions = @TopicPartition(topic = "partition-topic", partitions = {"0", "1"}),
            groupId = "demo-group-6"
    )
    public void consumeFromSpecificPartitions(
            ConsumerRecord<String, String> record,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {

        log.info("从指定分区{}接收到消息: key={}, value={}, offset={}",
                partition, record.key(), record.value(), record.offset());
        processMessage(record.value());
    }

    /**
     * 错误处理示例
     */
    @KafkaListener(topics = "error-topic", groupId = "demo-group-7")
    public void consumeMessageWithErrorHandling(String message) {
        try {
            log.info("处理可能出错的消息: {}", message);

            // 模拟处理可能出错的场景
            if (message.contains("error")) {
                throw new RuntimeException("模拟处理错误");
            }

            processMessage(message);

        } catch (Exception e) {
            log.error("消息处理出错: message={}, error={}", message, e.getMessage());
            // 可以将错误消息发送到死信队列或进行其他错误处理
            handleMessageError(message, e);
        }
    }

    /**
     * 处理字符串消息的业务逻辑
     */
    private void processMessage(String message) {
        // 这里实现具体的业务逻辑
        log.debug("处理消息: {}", message);

        // 模拟处理时间
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 处理对象消息的业务逻辑
     */
    private void processObjectMessage(Message message) {
        // 这里实现具体的对象消息处理逻辑
        log.debug("处理对象消息 - ID: {}, 发送者: {}, 内容: {}",
                message.getId(), message.getSender(), message.getContent());
    }

    /**
     * 错误处理逻辑
     */
    private void handleMessageError(String message, Exception e) {
        // 记录错误日志
        log.error("消息处理失败，将进行错误处理: message={}, error={}", message, e.getMessage());

        // 可以发送到死信队列
        // deadLetterProducer.sendToDeadLetter(message, e.getMessage());

        // 或者存储到数据库等
        // errorMessageRepository.save(new ErrorMessage(message, e.getMessage()));
    }
}
