package com.server.backend.websocket;

import com.server.backend.entity.Message;
import com.server.backend.services.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WebSocketController {

    private final ChatService chatService;

    @MessageMapping("/{groupId}/chat")
    @SendTo("/group/{groupId}/chat")
    public Message addMessageToChat(@Payload String message,
                                    @DestinationVariable Long groupId,
                                    SimpMessageHeaderAccessor header) {
        return chatService.addMessageToGroupChat(groupId, message,
                header.getFirstNativeHeader("username"));
    }

    @MessageMapping("/{groupId}/movie")
    @SendTo("/group/{groupId}/movie")
    public String moviePlayPause(@Payload String movieAction,
                                 @DestinationVariable Long groupId) {
        return movieAction;
    }

    @MessageMapping("/{groupId}/movie/rewind")
    @SendTo("/group/{groupId}/movie/rewind")
    public String rewindMovie(@Payload String time,
                             @DestinationVariable Long groupId) {
        return time;
    }
}
