package com.lpy.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String id;

    private String content;

    private String sender;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String topic;

    public Message(String content, String sender, String topic) {
        this.content = content;
        this.sender = sender;
        this.topic = topic;
        this.timestamp = LocalDateTime.now();
        this.id = System.currentTimeMillis() + "_" + sender;
    }
}