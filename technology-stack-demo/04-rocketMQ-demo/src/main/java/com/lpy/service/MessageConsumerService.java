package com.lpy.service;

import com.lpy.entity.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 消息消费者服务
 */
@Slf4j
@Service
public class MessageConsumerService {

    /**
     * 普通订单消息消费者
     */
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "ORDER_TOPIC",
            consumerGroup = "order-consumer-group",
            messageModel = MessageModel.CLUSTERING,
            consumeMode = ConsumeMode.CONCURRENTLY
    )
    public static class OrderMessageConsumer implements RocketMQListener<OrderMessage> {

        @Override
        public void onMessage(OrderMessage orderMessage) {
            log.info("===== 收到普通订单消息 =====");
            log.info("订单信息: {}", orderMessage);

            try {
                // 模拟业务处理
                processOrder(orderMessage);
                log.info("订单消息处理完成 - OrderId: {}", orderMessage.getOrderId());
            } catch (Exception e) {
                log.error("订单消息处理失败 - OrderMessage: {}", orderMessage, e);
                // 根据业务需要决定是否抛出异常进行重试
                // throw new RuntimeException("处理失败", e);
            }
        }

        private void processOrder(OrderMessage orderMessage) {
            // 模拟业务处理时间
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            log.info("处理订单业务逻辑 - OrderId: {}, UserId: {}, Amount: {}, Status: {}",
                    orderMessage.getOrderId(), orderMessage.getUserId(),
                    orderMessage.getAmount(), orderMessage.getStatusDesc());
        }
    }

    /**
     * 带Tag过滤的订单消息消费者
     */
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "ORDER_TOPIC",
            consumerGroup = "order-tag-consumer-group",
            selectorType = SelectorType.TAG,
            selectorExpression = "PAID || SHIPPED || COMPLETED"
    )
    public static class OrderTagConsumer implements RocketMQListener<OrderMessage> {

        @Override
        public void onMessage(OrderMessage orderMessage) {
            log.info("===== 收到Tag过滤订单消息 =====");
            log.info("订单信息: {}", orderMessage);

            // 根据订单状态进行不同处理
            switch (orderMessage.getStatus()) {
                case 2: // 已支付
                    handlePaidOrder(orderMessage);
                    break;
                case 3: // 已发货
                    handleShippedOrder(orderMessage);
                    break;
                case 5: // 已完成
                    handleCompletedOrder(orderMessage);
                    break;
                default:
                    log.warn("未识别的订单状态: {}", orderMessage.getStatus());
            }
        }

        private void handlePaidOrder(OrderMessage orderMessage) {
            log.info("处理已支付订单 - OrderId: {}", orderMessage.getOrderId());
            // 已支付订单的业务逻辑：库存扣减、发货准备等
        }

        private void handleShippedOrder(OrderMessage orderMessage) {
            log.info("处理已发货订单 - OrderId: {}", orderMessage.getOrderId());
            // 已发货订单的业务逻辑：物流跟踪、用户通知等
        }

        private void handleCompletedOrder(OrderMessage orderMessage) {
            log.info("处理已完成订单 - OrderId: {}", orderMessage.getOrderId());
            // 已完成订单的业务逻辑：积分发放、评价提醒等
        }
    }

    /**
     * 顺序订单消息消费者
     */
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "ORDER_ORDERLY_TOPIC",
            consumerGroup = "order-orderly-consumer-group",
            consumeMode = ConsumeMode.ORDERLY,
            messageModel = MessageModel.CLUSTERING
    )
    public static class OrderOrderlyConsumer implements RocketMQListener<OrderMessage> {

        @Override
        public void onMessage(OrderMessage orderMessage) {
            log.info("===== 收到顺序订单消息 =====");
            log.info("订单信息: {}", orderMessage);
            log.info("消费时间: {}", LocalDateTime.now());

            // 顺序处理订单状态变更
            processOrderSequentially(orderMessage);

            log.info("顺序消息处理完成 - OrderId: {}, Status: {}",
                    orderMessage.getOrderId(), orderMessage.getStatusDesc());
        }

        private void processOrderSequentially(OrderMessage orderMessage) {
            log.info("按顺序处理订单状态变更 - OrderId: {}, Status: {} -> {}",
                    orderMessage.getOrderId(), orderMessage.getStatus(), orderMessage.getStatusDesc());

            // 模拟顺序处理时间
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 这里可以添加状态变更的业务逻辑
            // 比如：订单状态机的状态流转验证
        }
    }

    /**
     * 广播消息消费者
     */
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "BROADCAST_TOPIC",
            consumerGroup = "broadcast-consumer-group",
            messageModel = MessageModel.BROADCASTING
    )
    public static class BroadcastConsumer implements RocketMQListener<String> {

        @Override
        public void onMessage(String message) {
            log.info("===== 收到广播消息 =====");
            log.info("广播内容: {}", message);
            log.info("消费时间: {}", LocalDateTime.now());

            // 处理广播消息，每个消费者实例都会收到
            processBroadcastMessage(message);
        }

        private void processBroadcastMessage(String message) {
            log.info("处理广播消息: {}", message);
            // 广播消息处理逻辑，比如：
            // 1. 系统配置更新通知
            // 2. 缓存刷新通知
            // 3. 服务器维护通知等
        }
    }

    /**
     * 延时消息消费者
     */
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "DELAY_TOPIC",
            consumerGroup = "delay-consumer-group",
            messageModel = MessageModel.CLUSTERING
    )
    public static class DelayMessageConsumer implements RocketMQListener<OrderMessage> {

        @Override
        public void onMessage(OrderMessage orderMessage) {
            log.info("===== 收到延时消息 =====");
            log.info("订单信息: {}", orderMessage);
            log.info("消费时间: {}", LocalDateTime.now());
            log.info("创建时间: {}", orderMessage.getCreateTime());

            // 处理延时消息业务逻辑
            processDelayMessage(orderMessage);
        }

        private void processDelayMessage(OrderMessage orderMessage) {
            log.info("处理延时消息业务逻辑 - OrderId: {}", orderMessage.getOrderId());

            // 延时消息的典型应用场景：
            // 1. 订单超时取消
            // 2. 定时任务触发
            // 3. 延迟通知等

            if ("待支付".equals(orderMessage.getStatusDesc())) {
                log.info("检查订单支付状态，可能需要自动取消订单 - OrderId: {}", orderMessage.getOrderId());
                // 这里可以查询订单最新状态，如果仍未支付则取消订单
            }
        }
    }

    /**
     * 事务消息消费者
     */
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "TRANSACTION_TOPIC",
            consumerGroup = "transaction-consumer-group",
            messageModel = MessageModel.CLUSTERING
    )
    public static class TransactionMessageConsumer implements RocketMQListener<OrderMessage> {

        @Override
        public void onMessage(OrderMessage orderMessage) {
            log.info("===== 收到事务消息 =====");
            log.info("订单信息: {}", orderMessage);

            try {
                // 处理事务消息
                processTransactionMessage(orderMessage);
                log.info("事务消息处理成功 - OrderId: {}", orderMessage.getOrderId());
            } catch (Exception e) {
                log.error("事务消息处理失败 - OrderMessage: {}", orderMessage, e);
                throw e; // 抛出异常进行重试
            }
        }

        private void processTransactionMessage(OrderMessage orderMessage) {
            log.info("处理事务消息 - OrderId: {}", orderMessage.getOrderId());

            // 事务消息的业务处理
            // 这里的逻辑应该是幂等的，因为消息可能会重复消费
        }
    }

    /**
     * 带消息头的消息消费者
     */
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "HEADER_TOPIC",
            consumerGroup = "header-consumer-group",
            messageModel = MessageModel.CLUSTERING
    )
    public static class HeaderMessageConsumer implements RocketMQListener<Message<OrderMessage>> {

        @Override
        public void onMessage(Message<OrderMessage> message) {
            log.info("===== 收到带消息头的消息 =====");

            // 获取消息体
            OrderMessage orderMessage = message.getPayload();
            log.info("订单信息: {}", orderMessage);

            // 获取消息头
            MessageHeaders headers = message.getHeaders();
            log.info("消息头信息: {}", headers);

            // 处理自定义消息头
            Object customHeader = headers.get("customKey");
            if (customHeader != null) {
                log.info("自定义消息头 customKey: {}", customHeader);
            }

            // 处理消息业务逻辑
            processHeaderMessage(orderMessage, headers);
        }

        private void processHeaderMessage(OrderMessage orderMessage, MessageHeaders headers) {
            log.info("处理带消息头的消息 - OrderId: {}", orderMessage.getOrderId());

            // 根据消息头进行特殊处理
            String source = (String) headers.get("source");
            if ("mobile".equals(source)) {
                log.info("来自移动端的订单消息");
            } else if ("web".equals(source)) {
                log.info("来自网页端的订单消息");
            }
        }
    }
}
