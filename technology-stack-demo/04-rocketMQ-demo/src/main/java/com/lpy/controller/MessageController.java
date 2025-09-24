package com.lpy.controller;

import cn.hutool.core.util.IdUtil;
import com.lpy.entity.OrderMessage;
import com.lpy.service.MessageProducerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 消息测试控制器
 * @author demo
 * @date 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageProducerService messageProducerService;

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", LocalDateTime.now());
        result.put("service", "SpringBoot RocketMQ Demo");
        return result;
    }

    /**
     * 发送同步消息
     */
    @PostMapping("/sync")
    public Map<String, Object> sendSyncMessage(
            @RequestParam(defaultValue = "ORDER_TOPIC") String topic,
            @RequestParam(required = false) String orderId) {

        OrderMessage orderMessage = createOrderMessage(orderId, 1);
        SendResult result = messageProducerService.sendSyncMessage(topic, orderMessage);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", result.getMsgId());
        response.put("sendStatus", result.getSendStatus());
        response.put("orderId", orderMessage.getOrderId());
        response.put("message", "同步消息发送成功");

        return response;
    }

    /**
     * 发送异步消息
     */
    @PostMapping("/async")
    public Map<String, Object> sendAsyncMessage(
            @RequestParam(defaultValue = "ORDER_TOPIC") String topic,
            @RequestParam(required = false) String orderId) {

        OrderMessage orderMessage = createOrderMessage(orderId, 1);
        CompletableFuture<SendResult> future = messageProducerService.sendAsyncMessage(topic, orderMessage);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("orderId", orderMessage.getOrderId());
        response.put("message", "异步消息发送中...");

        // 可以选择是否等待异步结果
        try {
            SendResult result = future.get(); // 等待异步结果
            response.put("messageId", result.getMsgId());
            response.put("sendStatus", result.getSendStatus());
        } catch (Exception e) {
            log.error("获取异步发送结果失败", e);
        }

        return response;
    }

    /**
     * 发送单向消息
     */
    @PostMapping("/oneway")
    public Map<String, Object> sendOneWayMessage(
            @RequestParam(defaultValue = "ORDER_TOPIC") String topic,
            @RequestParam(required = false) String orderId) {

        OrderMessage orderMessage = createOrderMessage(orderId, 1);
        messageProducerService.sendOneWayMessage(topic, orderMessage);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("orderId", orderMessage.getOrderId());
        response.put("message", "单向消息发送完成");

        return response;
    }

    /**
     * 发送延时消息
     */
    @PostMapping("/delay")
    public Map<String, Object> sendDelayMessage(
            @RequestParam(defaultValue = "DELAY_TOPIC") String topic,
            @RequestParam(defaultValue = "3") int delayLevel,
            @RequestParam(required = false) String orderId) {

        OrderMessage orderMessage = createOrderMessage(orderId, 1);
        orderMessage.setRemark("延时消息 - 延时等级: " + delayLevel);

        SendResult result = messageProducerService.sendDelayMessage(topic, orderMessage, delayLevel);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", result.getMsgId());
        response.put("orderId", orderMessage.getOrderId());
        response.put("delayLevel", delayLevel);
        response.put("message", "延时消息发送成功");

        return response;
    }

    /**
     * 发送带Tag的消息
     */
    @PostMapping("/tag")
    public Map<String, Object> sendTagMessage(
            @RequestParam(defaultValue = "ORDER_TOPIC") String topic,
            @RequestParam(defaultValue = "PAID") String tag,
            @RequestParam(required = false) String orderId) {

        // 根据tag设置不同的订单状态
        int status = getStatusByTag(tag);
        OrderMessage orderMessage = createOrderMessage(orderId, status);

        SendResult result = messageProducerService.sendMessageWithTag(topic, tag, orderMessage);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", result.getMsgId());
        response.put("orderId", orderMessage.getOrderId());
        response.put("tag", tag);
        response.put("status", orderMessage.getStatusDesc());
        response.put("message", "带Tag消息发送成功");

        return response;
    }

    /**
     * 发送顺序消息
     */
    @PostMapping("/orderly")
    public Map<String, Object> sendOrderlyMessage(
            @RequestParam(defaultValue = "ORDER_ORDERLY_TOPIC") String topic,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String orderId) {

        if (userId == null) {
            userId = "user_" + System.currentTimeMillis() % 1000;
        }

        OrderMessage orderMessage = createOrderMessage(orderId, 2);
        orderMessage.setUserId(userId);

        // 使用用户ID作为hash key，确保同一用户的消息有序
        SendResult result = messageProducerService.sendOrderlyMessage(topic, orderMessage, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", result.getMsgId());
        response.put("orderId", orderMessage.getOrderId());
        response.put("userId", userId);
        response.put("message", "顺序消息发送成功");

        return response;
    }

    /**
     * 发送事务消息
     */
    @PostMapping("/transaction")
    public Map<String, Object> sendTransactionMessage(
            @RequestParam(defaultValue = "TRANSACTION_TOPIC") String topic,
            @RequestParam(required = false) String orderId) {

        OrderMessage orderMessage = createOrderMessage(orderId, 2);
        orderMessage.setRemark("事务消息测试");

        // 事务参数，可以传递业务相关的信息
        Map<String, Object> transactionArg = new HashMap<>();
        transactionArg.put("orderId", orderMessage.getOrderId());
        transactionArg.put("timestamp", System.currentTimeMillis());

        SendResult result = messageProducerService.sendTransactionMessage(topic, orderMessage, transactionArg);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", result.getMsgId());
        response.put("orderId", orderMessage.getOrderId());
        response.put("sendStatus", result.getSendStatus());
        response.put("message", "事务消息发送完成");

        return response;
    }

    /**
     * 发送带自定义消息头的消息
     */
    @PostMapping("/header")
    public Map<String, Object> sendHeaderMessage(
            @RequestParam(defaultValue = "HEADER_TOPIC") String topic,
            @RequestParam(defaultValue = "web") String source,
            @RequestParam(required = false) String orderId) {

        OrderMessage orderMessage = createOrderMessage(orderId, 1);

        // 自定义消息头
        Map<String, Object> headers = new HashMap<>();
        headers.put("source", source);
        headers.put("version", "1.0.0");
//        headers.put("timestamp", System.currentTimeMillis());
        headers.put("customKey", "customValue");

        SendResult result = messageProducerService.sendMessageWithHeaders(topic, orderMessage, headers);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", result.getMsgId());
        response.put("orderId", orderMessage.getOrderId());
        response.put("headers", headers);
        response.put("message", "带消息头的消息发送成功");

        return response;
    }

    /**
     * 发送广播消息
     */
    @PostMapping("/broadcast")
    public Map<String, Object> sendBroadcastMessage(
            @RequestParam(defaultValue = "BROADCAST_TOPIC") String topic,
            @RequestParam(defaultValue = "系统维护通知") String message) {

        SendResult result = messageProducerService.sendSyncMessage(topic, message);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", result.getMsgId());
        response.put("content", message);
        response.put("message", "广播消息发送成功");

        return response;
    }

    /**
     * 批量发送消息
     */
    @PostMapping("/batch")
    public Map<String, Object> sendBatchMessages(
            @RequestParam(defaultValue = "ORDER_TOPIC") String topic,
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(defaultValue = "async") String sendType) {

        Map<String, Object> response = new HashMap<>();
        response.put("topic", topic);
        response.put("count", count);
        response.put("sendType", sendType);

        try {
            for (int i = 1; i <= count; i++) {
                OrderMessage orderMessage = createOrderMessage(null, 1);
                orderMessage.setRemark("批量消息测试 - " + i + "/" + count);

                if ("sync".equals(sendType)) {
                    messageProducerService.sendSyncMessage(topic, orderMessage);
                } else if ("async".equals(sendType)) {
                    messageProducerService.sendAsyncMessage(topic, orderMessage);
                } else {
                    messageProducerService.sendOneWayMessage(topic, orderMessage);
                }

                // 稍微延迟，避免消息发送过快
                Thread.sleep(100);
            }

            response.put("success", true);
            response.put("message", "批量发送 " + count + " 条消息成功");

        } catch (Exception e) {
            log.error("批量发送消息失败", e);
            response.put("success", false);
            response.put("message", "批量发送消息失败: " + e.getMessage());
        }

        return response;
    }

    /**
     * 发送不同状态的订单消息（演示状态流转）
     */
    @PostMapping("/flow")
    public Map<String, Object> sendOrderFlowMessages(
            @RequestParam(defaultValue = "ORDER_ORDERLY_TOPIC") String topic,
            @RequestParam(required = false) String userId) {

        if (userId == null) {
            userId = "user_" + System.currentTimeMillis() % 1000;
        }

        // 订单状态流转：待支付 -> 已支付 -> 已发货 -> 已收货 -> 已完成
        int[] statuses = {1, 2, 3, 4, 5};
        String[] tags = {"CREATED", "PAID", "SHIPPED", "RECEIVED", "COMPLETED"};

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("topic", topic);

        try {
            String baseOrderId = IdUtil.simpleUUID().substring(0, 8);

            for (int i = 0; i < statuses.length; i++) {
                OrderMessage orderMessage = createOrderMessage(baseOrderId, statuses[i]);
                orderMessage.setUserId(userId);
                orderMessage.setRemark("订单状态流转 - " + orderMessage.getStatusDesc());

                // 发送顺序消息，确保状态流转的顺序性
                messageProducerService.sendOrderlyMessage(topic, orderMessage, userId);

                log.info("发送订单状态流转消息 - OrderId: {}, Status: {}",
                        orderMessage.getOrderId(), orderMessage.getStatusDesc());

                // 状态流转间隔
                Thread.sleep(200);
            }

            response.put("success", true);
            response.put("message", "订单状态流转消息发送完成");

        } catch (Exception e) {
            log.error("发送订单状态流转消息失败", e);
            response.put("success", false);
            response.put("message", "发送失败: " + e.getMessage());
        }

        return response;
    }

    /**
     * 创建测试订单消息
     */
    private OrderMessage createOrderMessage(String orderId, int status) {
        if (orderId == null) {
            orderId = IdUtil.simpleUUID();
        }

        return OrderMessage.builder()
                .orderId(orderId)
                .userId("user_" + System.currentTimeMillis() % 1000)
                .productId("product_" + System.currentTimeMillis() % 100)
                .productName("测试商品_" + System.currentTimeMillis() % 50)
                .amount(new BigDecimal("99.99"))
                .quantity(1)
                .status(status)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .remark("测试订单消息")
                .build();
    }

    /**
     * 根据Tag获取对应的订单状态
     */
    private int getStatusByTag(String tag) {
        switch (tag.toUpperCase()) {
            case "CREATED":
                return 1;
            case "PAID":
                return 2;
            case "SHIPPED":
                return 3;
            case "RECEIVED":
                return 4;
            case "COMPLETED":
                return 5;
            case "CANCELLED":
                return 6;
            default:
                return 1;
        }
    }
}