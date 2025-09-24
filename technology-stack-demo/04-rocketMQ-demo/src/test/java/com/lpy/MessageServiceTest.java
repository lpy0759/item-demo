package com.lpy;

import cn.hutool.core.util.IdUtil;
import com.lpy.entity.OrderMessage;
import com.lpy.service.MessageProducerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 消息服务测试类
 * 注意：运行测试前请确保RocketMQ服务已启动
 * @author demo
 * @date 2024-01-01
 */
@Slf4j
@SpringBootTest
public class MessageServiceTest {

    @Autowired
    private MessageProducerService messageProducerService;

    /**
     * 测试同步发送消息
     */
    @Test
    public void testSyncSendMessage() throws InterruptedException {
        log.info("===== 测试同步发送消息 =====");

        OrderMessage orderMessage = createTestOrderMessage(1);

        SendResult result = messageProducerService.sendSyncMessage("ORDER_TOPIC", orderMessage);

        log.info("同步发送结果: {}", result);
        log.info("消息ID: {}", result.getMsgId());
        log.info("发送状态: {}", result.getSendStatus());

        // 等待消费者处理
        Thread.sleep(2000);
    }

    /**
     * 测试异步发送消息
     */
    @Test
    public void testAsyncSendMessage() throws Exception {
        log.info("===== 测试异步发送消息 =====");

        OrderMessage orderMessage = createTestOrderMessage(1);

        CompletableFuture<SendResult> future = messageProducerService.sendAsyncMessage("ORDER_TOPIC", orderMessage);

        // 等待异步结果
        SendResult result = future.get(5, TimeUnit.SECONDS);

        log.info("异步发送结果: {}", result);
        log.info("消息ID: {}", result.getMsgId());
        log.info("发送状态: {}", result.getSendStatus());

        // 等待消费者处理
        Thread.sleep(2000);
    }

    /**
     * 测试单向发送消息
     */
    @Test
    public void testOneWaySendMessage() throws InterruptedException {
        log.info("===== 测试单向发送消息 =====");

        OrderMessage orderMessage = createTestOrderMessage(1);

        messageProducerService.sendOneWayMessage("ORDER_TOPIC", orderMessage);

        log.info("单向消息发送完成");

        // 等待消费者处理
        Thread.sleep(2000);
    }

    /**
     * 测试延时消息
     */
    @Test
    public void testDelayMessage() throws InterruptedException {
        log.info("===== 测试延时消息 =====");

        OrderMessage orderMessage = createTestOrderMessage(1);
        orderMessage.setRemark("延时消息测试 - 10秒延时");

        log.info("发送延时消息，当前时间: {}", LocalDateTime.now());

        SendResult result = messageProducerService.sendDelayMessage("DELAY_TOPIC", orderMessage, 3); // 延时等级3 = 10秒

        log.info("延时消息发送结果: {}", result);

        // 等待延时消息到达
        Thread.sleep(15000);
    }

    /**
     * 测试Tag消息
     */
    @Test
    public void testTagMessage() throws InterruptedException {
        log.info("===== 测试Tag消息 =====");

        String[] tags = {"PAID", "SHIPPED", "COMPLETED"};
        int[] statuses = {2, 3, 5};

        for (int i = 0; i < tags.length; i++) {
            OrderMessage orderMessage = createTestOrderMessage(statuses[i]);

            SendResult result = messageProducerService.sendMessageWithTag("ORDER_TOPIC", tags[i], orderMessage);

            log.info("Tag消息发送成功 - Tag: {}, MessageId: {}", tags[i], result.getMsgId());

            // 间隔发送
            Thread.sleep(500);
        }

        // 等待消费者处理
        Thread.sleep(3000);
    }

    /**
     * 测试顺序消息
     */
    @Test
    public void testOrderlyMessage() throws InterruptedException {
        log.info("===== 测试顺序消息 =====");

        String userId = "test_user_" + System.currentTimeMillis();
        int[] statuses = {1, 2, 3, 4, 5}; // 订单状态流转
        String[] statusNames = {"创建", "支付", "发货", "收货", "完成"};

        for (int i = 0; i < statuses.length; i++) {
            OrderMessage orderMessage = createTestOrderMessage(statuses[i]);
            orderMessage.setUserId(userId);
            orderMessage.setRemark("顺序消息测试 - " + statusNames[i]);

            SendResult result = messageProducerService.sendOrderlyMessage("ORDER_ORDERLY_TOPIC", orderMessage, userId);

            log.info("顺序消息发送成功 - Status: {}, MessageId: {}", statusNames[i], result.getMsgId());

            // 稍微间隔
            Thread.sleep(200);
        }

        // 等待消费者处理
        Thread.sleep(5000);
    }

    /**
     * 测试事务消息
     */
    @Test
    public void testTransactionMessage() throws InterruptedException {
        log.info("===== 测试事务消息 =====");

        OrderMessage orderMessage = createTestOrderMessage(2);
        orderMessage.setRemark("事务消息测试");

        Map<String, Object> transactionArg = new HashMap<>();
        transactionArg.put("orderId", orderMessage.getOrderId());
        transactionArg.put("userId", orderMessage.getUserId());
        transactionArg.put("timestamp", System.currentTimeMillis());

        SendResult result = messageProducerService.sendTransactionMessage("TRANSACTION_TOPIC", orderMessage, transactionArg);

        log.info("事务消息发送结果: {}", result);
        log.info("发送状态: {}", result.getSendStatus());

        // 等待事务确认和消费者处理
        Thread.sleep(10000);
    }

    /**
     * 测试自定义消息头
     */
    @Test
    public void testHeaderMessage() throws InterruptedException {
        log.info("===== 测试自定义消息头 =====");

        OrderMessage orderMessage = createTestOrderMessage(1);

        Map<String, Object> headers = new HashMap<>();
        headers.put("source", "mobile");
        headers.put("version", "2.0.0");
        headers.put("userId", orderMessage.getUserId());
        headers.put("timestamp", System.currentTimeMillis());
        headers.put("customKey", "测试自定义值");

        SendResult result = messageProducerService.sendMessageWithHeaders("HEADER_TOPIC", orderMessage, headers);

        log.info("自定义头消息发送成功 - MessageId: {}", result.getMsgId());

        // 等待消费者处理
        Thread.sleep(3000);
    }

    /**
     * 测试广播消息
     */
    @Test
    public void testBroadcastMessage() throws InterruptedException {
        log.info("===== 测试广播消息 =====");

        String broadcastMessage = "系统维护通知 - " + LocalDateTime.now();

        SendResult result = messageProducerService.sendSyncMessage("BROADCAST_TOPIC", broadcastMessage);

        log.info("广播消息发送成功 - MessageId: {}", result.getMsgId());

        // 等待消费者处理
        Thread.sleep(3000);
    }

    /**
     * 测试批量发送消息
     */
    @Test
    public void testBatchSendMessage() throws InterruptedException {
        log.info("===== 测试批量发送消息 =====");

        int messageCount = 10;
        CountDownLatch latch = new CountDownLatch(messageCount);

        for (int i = 1; i <= messageCount; i++) {
            final int index = i;

            new Thread(() -> {
                try {
                    OrderMessage orderMessage = createTestOrderMessage(1);
                    orderMessage.setRemark("批量消息测试 - " + index + "/" + messageCount);

                    messageProducerService.sendAsyncMessage("ORDER_TOPIC", orderMessage);

                    log.info("批量消息 {} 发送完成", index);
                } catch (Exception e) {
                    log.error("批量消息 {} 发送失败", index, e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // 等待所有消息发送完成
        latch.await(30, TimeUnit.SECONDS);

        log.info("批量发送消息测试完成，总数: {}", messageCount);

        // 等待消费者处理
        Thread.sleep(10000);
    }

    /**
     * 性能测试
     */
    @Test
    public void testPerformance() throws InterruptedException {
        log.info("===== 性能测试 =====");

        int messageCount = 1000;
        long startTime = System.currentTimeMillis();

        CountDownLatch latch = new CountDownLatch(messageCount);

        // 使用多线程发送消息
        for (int i = 1; i <= messageCount; i++) {
            final int index = i;

            new Thread(() -> {
                try {
                    OrderMessage orderMessage = createTestOrderMessage(1);
                    orderMessage.setRemark("性能测试 - " + index);

                    // 使用单向发送提高性能
                    messageProducerService.sendOneWayMessage("ORDER_TOPIC", orderMessage);

                    if (index % 100 == 0) {
                        log.info("已发送消息数: {}", index);
                    }
                } catch (Exception e) {
                    log.error("性能测试消息 {} 发送失败", index, e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // 等待所有消息发送完成
        latch.await(60, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("=== 性能测试结果 ===");
        log.info("消息数量: {}", messageCount);
        log.info("耗时: {} ms", duration);
        log.info("平均TPS: {}", messageCount * 1000 / duration);
        log.info("平均耗时: {} ms/msg", (double) duration / messageCount);

        // 等待消费者处理
        Thread.sleep(15000);
    }

    /**
     * 测试消息发送异常处理
     */
    @Test
    public void testExceptionHandling() {
        log.info("===== 测试异常处理 =====");

        try {
            // 发送到不存在的topic（如果autoCreateTopicEnable=false）
            OrderMessage orderMessage = createTestOrderMessage(1);
            messageProducerService.sendSyncMessage("NON_EXIST_TOPIC", orderMessage);
        } catch (Exception e) {
            log.info("预期的异常被捕获: {}", e.getMessage());
        }

        try {
            // 发送空消息
            messageProducerService.sendSyncMessage("ORDER_TOPIC", null);
        } catch (Exception e) {
            log.info("空消息异常被捕获: {}", e.getMessage());
        }
    }

    /**
     * 创建测试订单消息
     */
    private OrderMessage createTestOrderMessage(int status) {
        return OrderMessage.builder()
                .orderId(IdUtil.simpleUUID())
                .userId("test_user_" + System.currentTimeMillis() % 1000)
                .productId("test_product_" + System.currentTimeMillis() % 100)
                .productName("测试商品_" + System.currentTimeMillis() % 50)
                .amount(new BigDecimal("99.99"))
                .quantity(1)
                .status(status)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .remark("单元测试订单消息")
                .build();
    }
}
