package com.lpy.controller;


import com.lpy.entity.Message;
import com.lpy.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kafka")
@Slf4j
public class KafkaController {

    @Autowired
    private KafkaProducerService producerService;

    /**
     * 发送简单消息
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam String topic,
            @RequestParam String message) {

        try {
            producerService.sendMessage(topic, message);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "消息发送成功");
            response.put("topic", topic);
            response.put("content", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "消息发送失败: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 发送带键的消息
     */
    @PostMapping("/send-with-key")
    public ResponseEntity<Map<String, Object>> sendMessageWithKey(
            @RequestParam String topic,
            @RequestParam String key,
            @RequestParam String message) {

        try {
            producerService.sendMessage(topic, key, message);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "带键消息发送成功");
            response.put("topic", topic);
            response.put("key", key);
            response.put("content", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("发送带键消息失败: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "带键消息发送失败: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 发送对象消息
     */
    @PostMapping("/send-object")
    public ResponseEntity<Map<String, Object>> sendObjectMessage(@RequestBody Message message) {

        try {
            // 如果没有设置topic，使用默认的
            if (message.getTopic() == null || message.getTopic().isEmpty()) {
                message.setTopic("object-topic");
            }

            producerService.sendMessage(message.getTopic(), message);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "对象消息发送成功");
            response.put("data", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("发送对象消息失败: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "对象消息发送失败: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 批量发送消息
     */
    @PostMapping("/send-batch")
    public ResponseEntity<Map<String, Object>> sendBatchMessages(
            @RequestParam String topic,
            @RequestBody List<String> messages) {

        try {
            producerService.sendBatchMessages(topic, messages);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量消息发送成功");
            response.put("topic", topic);
            response.put("count", messages.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("批量发送消息失败: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量消息发送失败: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 快速测试接口
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testKafka() {
        try {
            // 发送测试消息
            producerService.sendMessage("test-topic", "这是一条测试消息");

            // 发送对象消息
            Message objectMessage = new Message("测试对象消息", "system", "object-topic");
            producerService.sendMessage("object-topic", objectMessage);

            // 批量发送
            List<String> batchMessages = Arrays.asList("批量消息1", "批量消息2", "批量消息3");
            producerService.sendBatchMessages("batch-topic", batchMessages);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kafka测试完成，已发送多种类型的测试消息");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Kafka测试失败: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Kafka测试失败: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取Kafka状态信息
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getKafkaStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("description", "Kafka集成服务运行正常");
        status.put("supportedTopics", Arrays.asList(
                "test-topic", "test-detail-topic", "object-topic",
                "manual-ack-topic", "batch-topic", "partition-topic", "error-topic"
        ));

        return ResponseEntity.ok(status);
    }
}