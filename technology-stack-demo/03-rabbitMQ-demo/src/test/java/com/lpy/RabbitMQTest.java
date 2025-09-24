package com.lpy;




import com.lpy.service.MessageProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RabbitMQTest {

    @Autowired
    private MessageProducer messageProducer;

    @Test
    public void testDirectMessage() throws InterruptedException {
        messageProducer.sendDirectMessage("这是一个Direct消息测试");
        Thread.sleep(3000); // 等待消息消费
    }

    @Test
    public void testTopicMessage() throws InterruptedException {
        messageProducer.sendTopicMessage("topic.key.user", "用户相关的Topic消息");
        messageProducer.sendTopicMessage("topic.key.order", "订单相关的Topic消息");
        Thread.sleep(3000); // 等待消息消费
    }

    @Test
    public void testFanoutMessage() throws InterruptedException {
        messageProducer.sendFanoutMessage("这是一个Fanout广播消息");
        Thread.sleep(3000); // 等待消息消费
    }
}
