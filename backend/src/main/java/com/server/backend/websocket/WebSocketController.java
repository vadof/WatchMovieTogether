package com.server.backend.websocket;

import com.server.backend.entity.Chat;
import com.server.backend.entity.Message;
import com.server.backend.services.ChatService;
import com.server.backend.services.GroupService;
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

    private final GroupService groupService;
    private final ChatService chatService;

    @MessageMapping("/chat/{groupId}")
    @SendTo("/group/{groupId}/chat")
    public Message addMessageToChat(@Payload String message,
                                    @DestinationVariable Long groupId,
                                    SimpMessageHeaderAccessor header) {
        Chat chat = groupService.getGroupChat(groupId).orElseThrow();
        return chatService.addMessageToChat(chat.getId(), message,
                header.getFirstNativeHeader("username"));
    }

    @MessageMapping("/movie/{groupId}")
    @SendTo("/group/{groupId}/movie")
    public String moviePlayPause(@Payload String movieAction,
                                      @DestinationVariable Long groupId) {
        return movieAction;
    }
}
