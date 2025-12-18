package com.tms.backend.controller;

import com.tms.backend.dto.SignalingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SignalingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/signal")
    public void processSignal(@Payload SignalingMessage message) {
        // Route the message to the specific receiver via a topic
        messagingTemplate.convertAndSend(
                "/topic/signal/" + message.getReceiverId(),
                message
        );
    }
}
