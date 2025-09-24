package com.lpy.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 队列名称常量
    public static final String DIRECT_QUEUE = "direct.queue";
    public static final String TOPIC_QUEUE_1 = "topic.queue.1";
    public static final String TOPIC_QUEUE_2 = "topic.queue.2";
    public static final String FANOUT_QUEUE_1 = "fanout.queue.1";
    public static final String FANOUT_QUEUE_2 = "fanout.queue.2";

    // 交换机名称常量
    public static final String DIRECT_EXCHANGE = "direct.exchange";
    public static final String TOPIC_EXCHANGE = "topic.exchange";
    public static final String FANOUT_EXCHANGE = "fanout.exchange";

    // 路由键常量
    public static final String DIRECT_ROUTING_KEY = "direct.key";
    public static final String TOPIC_ROUTING_KEY_1 = "topic.key.user";
    public static final String TOPIC_ROUTING_KEY_2 = "topic.key.order";


    // 消息转换器
    @Bean
    public MessageConverter messageConverter() {
//        ObjectMapper mapper = new ObjectMapper();
//        // 支持 Java 8 时间类
//        mapper.registerModule(new JavaTimeModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate配置
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        // 开启发布确认
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息发送成功");
            } else {
                System.out.println("消息发送失败: " + cause);
            }
        });
        // 开启发布退回
        template.setReturnsCallback((returned) -> {
            System.out.println("消息被退回: " + returned.getMessage());
        });
        return template;
    }

    // 监听器容器工厂
    @Bean
    public RabbitListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    // ========== Direct Exchange 配置 ==========
    @Bean
    public Queue directQueue() {
        return QueueBuilder.durable(DIRECT_QUEUE).build();
    }

    @Bean
    public DirectExchange directExchange() {
        return ExchangeBuilder.directExchange(DIRECT_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding directBinding() {
        return BindingBuilder.bind(directQueue())
                .to(directExchange())
                .with(DIRECT_ROUTING_KEY);
    }

    // ========== Topic Exchange 配置 ==========
    @Bean
    public Queue topicQueue1() {
        return QueueBuilder.durable(TOPIC_QUEUE_1).build();
    }

    @Bean
    public Queue topicQueue2() {
        return QueueBuilder.durable(TOPIC_QUEUE_2).build();
    }

    @Bean
    public TopicExchange topicExchange() {
        return ExchangeBuilder.topicExchange(TOPIC_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1())
                .to(topicExchange())
                .with("topic.key.*");
    }

    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2())
                .to(topicExchange())
                .with("topic.key.order");
    }

    // ========== Fanout Exchange 配置 ==========
    @Bean
    public Queue fanoutQueue1() {
        return QueueBuilder.durable(FANOUT_QUEUE_1).build();
    }

    @Bean
    public Queue fanoutQueue2() {
        return QueueBuilder.durable(FANOUT_QUEUE_2).build();
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return ExchangeBuilder.fanoutExchange(FANOUT_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }
}
