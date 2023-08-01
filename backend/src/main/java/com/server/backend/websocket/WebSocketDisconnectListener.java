package com.server.backend.websocket;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@AllArgsConstructor
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final WebSocketService webSocketService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        this.webSocketService.removeUser(event);
    }

}
