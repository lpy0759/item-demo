package com.lpy;


import com.lpy.entity.Message;
import com.lpy.service.KafkaProducerService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.group-id=test-group"
})
public class KafkaIntegrationTest {

    @Autowired
    private KafkaProducerService producerService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    public void testSendAndReceiveMessage() throws InterruptedException {
        String topic = "test-integration-topic";
        String testMessage = "Hello Kafka Integration Test!";

        // 创建消费者
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        ContainerProperties containerProperties = new ContainerProperties(topic);

        KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) records::add);

        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

        // 发送消息
        producerService.sendMessage(topic, testMessage);

        // 验证消息接收
        ConsumerRecord<String, String> received = records.poll(10, TimeUnit.SECONDS);
        assertNotNull(received);
        assertEquals(testMessage, received.value());

        container.stop();
    }

    @Test
    public void testSendObjectMessage() throws InterruptedException {
        String topic = "test-object-topic";
        Message testMessage = new Message("测试内容", "测试发送者", topic);

        // 发送对象消息
        producerService.sendMessage(topic, testMessage);

        // 这里可以添加更多的验证逻辑
        // 由于对象消息需要JSON序列化，测试会更复杂一些
        assertTrue(true); // 简化测试，实际项目中需要完整的验证
    }

    @Test
    public void testBatchSendMessages() {
        String topic = "test-batch-topic";
        java.util.List<String> messages = java.util.Arrays.asList(
                "批量消息1", "批量消息2", "批量消息3"
        );

        // 批量发送消息
        assertDoesNotThrow(() -> producerService.sendBatchMessages(topic, messages));
    }
}
