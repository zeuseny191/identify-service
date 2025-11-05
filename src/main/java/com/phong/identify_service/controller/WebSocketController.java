package com.phong.identify_service.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/sendMessage") // Endpoint nhận tin nhắn từ client (tương ứng với /app/sendMessage)
    @SendTo("/topic/messages")    // Gửi kết quả đến các client subscribe topic này
    public String handleMessage(String message) {
        // Xử lý tin nhắn và trả về kết quả
        return "Server đã nhận: " + message;
    }
}
