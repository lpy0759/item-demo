package com.lpy.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ配置类
 */
@Slf4j
@Configuration
public class RocketMQConfig {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 应用启动后执行的初始化操作
     */
    @Bean
    public CommandLineRunner rocketMQInitRunner() {
        return args -> {
            log.info("==========================================");
            log.info("RocketMQ配置初始化完成");
            log.info("Producer Group: {}", rocketMQTemplate.getProducer().getProducerGroup());
            log.info("NameServer Address: {}", rocketMQTemplate.getProducer().getNamesrvAddr());
            log.info("Send Message Timeout: {}", rocketMQTemplate.getProducer().getSendMsgTimeout());
            log.info("==========================================");
        };
    }

    /**
     * RocketMQ常用主题配置
     */
    public static final class Topics {
        public static final String ORDER_TOPIC = "ORDER_TOPIC";
        public static final String ORDER_ORDERLY_TOPIC = "ORDER_ORDERLY_TOPIC";
        public static final String BROADCAST_TOPIC = "BROADCAST_TOPIC";
        public static final String DELAY_TOPIC = "DELAY_TOPIC";
    }

    /**
     * RocketMQ常用Tag配置
     */
    public static final class Tags {
        public static final String ORDER_CREATED = "CREATED";
        public static final String ORDER_PAID = "PAID";
        public static final String ORDER_SHIPPED = "SHIPPED";
        public static final String ORDER_COMPLETED = "COMPLETED";
        public static final String ORDER_CANCELLED = "CANCELLED";
    }

    /**
     * RocketMQ消费者组配置
     */
    public static final class ConsumerGroups {
        public static final String ORDER_CONSUMER_GROUP = "order-consumer-group";
        public static final String ORDER_TAG_CONSUMER_GROUP = "order-tag-consumer-group";
        public static final String ORDER_ORDERLY_CONSUMER_GROUP = "order-orderly-consumer-group";
        public static final String BROADCAST_CONSUMER_GROUP = "broadcast-consumer-group";
    }

    /**
     * 延时等级配置
     * RocketMQ支持的延时等级：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    public static final class DelayLevels {
        public static final int LEVEL_1S = 1;    // 1秒
        public static final int LEVEL_5S = 2;    // 5秒
        public static final int LEVEL_10S = 3;   // 10秒
        public static final int LEVEL_30S = 4;   // 30秒
        public static final int LEVEL_1M = 5;    // 1分钟
        public static final int LEVEL_2M = 6;    // 2分钟
        public static final int LEVEL_3M = 7;    // 3分钟
        public static final int LEVEL_4M = 8;    // 4分钟
        public static final int LEVEL_5M = 9;    // 5分钟
        public static final int LEVEL_6M = 10;   // 6分钟
        public static final int LEVEL_7M = 11;   // 7分钟
        public static final int LEVEL_8M = 12;   // 8分钟
        public static final int LEVEL_9M = 13;   // 9分钟
        public static final int LEVEL_10M = 14;  // 10分钟
        public static final int LEVEL_20M = 15;  // 20分钟
        public static final int LEVEL_30M = 16;  // 30分钟
        public static final int LEVEL_1H = 17;   // 1小时
        public static final int LEVEL_2H = 18;   // 2小时
    }
}