package com.lpy.listener;

import com.lpy.entity.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 订单事务消息监听器
 * @author demo
 * @date 2024-01-01
 */
@Slf4j
@Component
@RocketMQTransactionListener
public class OrderTransactionListener implements RocketMQLocalTransactionListener {

    /**
     * 执行本地事务
     * 当发送事务消息时会调用此方法
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        try {
            log.info("===== 执行本地事务 =====");
            log.info("消息: {}", message);
            log.info("参数: {}", arg);

            // 获取消息内容
            Object payload = message.getPayload();
            if (payload instanceof OrderMessage) {
                OrderMessage orderMessage = (OrderMessage) payload;
                log.info("处理订单事务 - OrderId: {}", orderMessage.getOrderId());

                // 执行本地事务业务逻辑
                boolean success = executeOrderTransaction(orderMessage, arg);

                if (success) {
                    log.info("本地事务执行成功 - OrderId: {}", orderMessage.getOrderId());
                    return RocketMQLocalTransactionState.COMMIT;
                } else {
                    log.warn("本地事务执行失败 - OrderId: {}", orderMessage.getOrderId());
                    return RocketMQLocalTransactionState.ROLLBACK;
                }
            }

            return RocketMQLocalTransactionState.UNKNOWN;

        } catch (Exception e) {
            log.error("执行本地事务异常", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 检查本地事务状态
     * 当MQ服务器回查事务状态时会调用此方法
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        try {
            log.info("===== 检查本地事务状态 =====");
            log.info("消息: {}", message);

            // 获取消息内容
            Object payload = message.getPayload();
            if (payload instanceof OrderMessage) {
                OrderMessage orderMessage = (OrderMessage) payload;
                log.info("检查订单事务状态 - OrderId: {}", orderMessage.getOrderId());

                // 查询本地事务执行状态
                TransactionStatus status = checkOrderTransactionStatus(orderMessage.getOrderId());

                switch (status) {
                    case SUCCESS:
                        log.info("本地事务已成功 - OrderId: {}", orderMessage.getOrderId());
                        return RocketMQLocalTransactionState.COMMIT;
                    case FAILED:
                        log.warn("本地事务已失败 - OrderId: {}", orderMessage.getOrderId());
                        return RocketMQLocalTransactionState.ROLLBACK;
                    case UNKNOWN:
                    default:
                        log.info("本地事务状态未知 - OrderId: {}", orderMessage.getOrderId());
                        return RocketMQLocalTransactionState.UNKNOWN;
                }
            }

            return RocketMQLocalTransactionState.UNKNOWN;

        } catch (Exception e) {
            log.error("检查本地事务状态异常", e);
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }

    /**
     * 执行订单事务业务逻辑
     */
    private boolean executeOrderTransaction(OrderMessage orderMessage, Object arg) {
        try {
            log.info("开始执行订单事务业务逻辑 - OrderId: {}", orderMessage.getOrderId());

            // 模拟本地事务操作
            // 1. 数据库操作
            // 2. 调用第三方服务
            // 3. 其他业务逻辑

            // 模拟业务处理时间
            Thread.sleep(1000);

            // 模拟事务成功/失败的逻辑
            // 这里简单地根据订单ID的哈希值来决定成功或失败
            String orderId = orderMessage.getOrderId();
            boolean success = orderId.hashCode() % 2 == 0;

            if (success) {
                // 保存事务执行状态到数据库或缓存
                saveTransactionStatus(orderId, TransactionStatus.SUCCESS);
                log.info("订单事务执行成功 - OrderId: {}", orderId);
            } else {
                saveTransactionStatus(orderId, TransactionStatus.FAILED);
                log.warn("订单事务执行失败 - OrderId: {}", orderId);
            }

            return success;

        } catch (Exception e) {
            log.error("执行订单事务业务逻辑异常 - OrderId: {}", orderMessage.getOrderId(), e);
            saveTransactionStatus(orderMessage.getOrderId(), TransactionStatus.FAILED);
            return false;
        }
    }

    /**
     * 检查订单事务状态
     */
    private TransactionStatus checkOrderTransactionStatus(String orderId) {
        try {
            log.info("查询订单事务状态 - OrderId: {}", orderId);

            // 这里应该从数据库或缓存中查询事务状态
            // 为了演示，这里使用简单的逻辑

            // 模拟查询数据库
            Thread.sleep(100);

            // 模拟查询结果
            int hash = orderId.hashCode();
            if (hash % 3 == 0) {
                return TransactionStatus.SUCCESS;
            } else if (hash % 3 == 1) {
                return TransactionStatus.FAILED;
            } else {
                return TransactionStatus.UNKNOWN;
            }

        } catch (Exception e) {
            log.error("查询订单事务状态异常 - OrderId: {}", orderId, e);
            return TransactionStatus.UNKNOWN;
        }
    }

    /**
     * 保存事务状态
     */
    private void saveTransactionStatus(String orderId, TransactionStatus status) {
        try {
            log.info("保存事务状态 - OrderId: {}, Status: {}", orderId, status);

            // 这里应该将事务状态保存到数据库或缓存中
            // 实际实现中可以使用Redis、数据库等持久化存储

            // 模拟保存操作
            Thread.sleep(50);

        } catch (Exception e) {
            log.error("保存事务状态异常 - OrderId: {}, Status: {}", orderId, status, e);
        }
    }

    /**
     * 事务状态枚举
     */
    public enum TransactionStatus {
        SUCCESS("成功"),
        FAILED("失败"),
        UNKNOWN("未知");

        private final String description;

        TransactionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return this.name() + "(" + description + ")";
        }
    }
}
