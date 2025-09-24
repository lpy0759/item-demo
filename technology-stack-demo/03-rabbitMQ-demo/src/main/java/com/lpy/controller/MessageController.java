package com.lpy.controller;

import com.lpy.service.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageProducer messageProducer;

    /**
     * 发送Direct消息
     */
    @PostMapping("/direct")
    public String sendDirectMessage(@RequestParam String content) {
        messageProducer.sendDirectMessage(content);
        return "Direct消息发送成功";
    }

    /**
     * 发送Topic消息
     */
    @PostMapping("/topic")
    public String sendTopicMessage(@RequestParam String routingKey,
                                   @RequestParam String content) {
        messageProducer.sendTopicMessage(routingKey, content);
        return "Topic消息发送成功";
    }

    /**
     * 发送Fanout消息
     */
    @PostMapping("/fanout")
    public String sendFanoutMessage(@RequestParam String content) {
        messageProducer.sendFanoutMessage(content);
        return "Fanout消息发送成功";
    }
}
