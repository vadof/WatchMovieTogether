package com.server.backend.websocket;

import com.server.backend.entity.Message;
import com.server.backend.entity.User;
import com.server.backend.services.ChatService;
import com.server.backend.services.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
@AllArgsConstructor
public class WebSocketController {

    private final GroupService groupService;
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

    @MessageMapping("/{groupId}/user/privileges")
    @SendTo("/group/{groupId}/user/privileges")
    public Set<User> changeUserPrivileges(@Payload User user,
                                          @DestinationVariable Long groupId,
                                          SimpMessageHeaderAccessor header) {
        return this.groupService.changeUserPrivileges(user,
                groupId, header.getFirstNativeHeader("username"));
    }

    @MessageMapping("/{groupId}/user/leave")
    @SendTo("/group/{groupId}/user/leave")
    public User removeUserFromGroup(@Payload User user,
                                    @DestinationVariable Long groupId,
                                    SimpMessageHeaderAccessor headerAccessor) {
        return this.groupService.removeUserFromGroup(groupId,
                user, headerAccessor.getFirstNativeHeader("username"));
    }

    @MessageMapping("/{groupId}/user/add")
    @SendTo("/group/{groupId}/user/add")
    public void addUserToGroup(@Payload User user,
                               @DestinationVariable Long groupId) {
        this.groupService.addUserToGroup(groupId, user);
    }
}
