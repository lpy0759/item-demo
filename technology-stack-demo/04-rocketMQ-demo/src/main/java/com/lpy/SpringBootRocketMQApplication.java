package com.lpy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot RocketMQ 示例应用
 */
@Slf4j
@SpringBootApplication
public class SpringBootRocketMQApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRocketMQApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("==========================================");
        log.info("Spring Boot RocketMQ Demo 启动成功!");
        log.info("应用端口: 8080");
        log.info("健康检查: http://localhost:8080/message/health");
        log.info("API文档地址:");
        log.info("  同步消息: POST /message/sync");
        log.info("  异步消息: POST /message/async");
        log.info("  单向消息: POST /message/oneway");
        log.info("  延时消息: POST /message/delay");
        log.info("  Tag消息:  POST /message/tag");
        log.info("  顺序消息: POST /message/orderly");
        log.info("  事务消息: POST /message/transaction");
        log.info("  头消息:   POST /message/header");
        log.info("  广播消息: POST /message/broadcast");
        log.info("  批量消息: POST /message/batch");
        log.info("  状态流转: POST /message/flow");
        log.info("==========================================");
    }
}
