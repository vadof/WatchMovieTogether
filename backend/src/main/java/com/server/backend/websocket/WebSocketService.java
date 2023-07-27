package com.server.backend.websocket;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendObjectByWebsocket(String destination, Object o) {
        simpMessagingTemplate.convertAndSend(destination, o);
    }

}
