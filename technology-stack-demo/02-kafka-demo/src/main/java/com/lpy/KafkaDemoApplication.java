package com.lpy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@Slf4j
public class KafkaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
        log.info("=========================================");
        log.info("  Kafka Demo Application Started!      ");
        log.info("  API文档: http://localhost:8080/api/kafka/status");
        log.info("=========================================");
    }
}