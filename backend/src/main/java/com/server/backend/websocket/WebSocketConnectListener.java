package com.server.backend.websocket;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
@AllArgsConstructor
public class WebSocketConnectListener implements ApplicationListener<SessionConnectedEvent> {

    private final WebSocketService webSocketService;

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {;
        this.webSocketService.addUser(event);
    }
}
