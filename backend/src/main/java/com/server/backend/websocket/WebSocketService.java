package com.server.backend.websocket;

import com.server.backend.entity.Group;
import com.server.backend.entity.User;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GroupRepository groupRepository;
    private final JwtService jwtService;

    private final Map<Long, GroupSession> groupSessionMap = new HashMap<>();
    private final Map<String, User> sessionUserMap = new HashMap<>();
    private final Map<User, Long> userGroupMap = new HashMap<>();

    public void sendObjectByWebsocket(String destination, Object o) {
        simpMessagingTemplate.convertAndSend(destination, o);
    }

    public void addUser(SessionConnectedEvent event) {
        Map<String, String> nativeHeaders = extractNativeHeadersFromString(event.getMessage().getHeaders());

        Group group = groupRepository.findById(Long.parseLong(nativeHeaders.get("groupId"))).orElseThrow();
        User user = jwtService.getUserFromBearerToken(nativeHeaders.get("Authorization")).orElseThrow();
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");

        if (group.getUsers().contains(user)) {
            GroupSession groupSession = groupSessionMap.get(group.getId());
            if (groupSession == null) {
                groupSession = new GroupSession(group.getId());
                this.groupSessionMap.put(group.getId(), groupSession);
            }

            groupSession.addUser(user);
            this.sessionUserMap.put(sessionId, user);
            this.userGroupMap.put(user, group.getId());
        }
    }

    public void removeUser(SessionDisconnectEvent event) {
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");


        User user = this.sessionUserMap.get(sessionId);
        Long groupId = this.userGroupMap.get(user);
        GroupSession groupSession = this.groupSessionMap.get(groupId);

        groupSession.removeUser(user);
        if (groupSession.getUsers().isEmpty()) {
            this.groupSessionMap.remove(groupSession.getGroupId());
        }

        this.sessionUserMap.remove(sessionId);
        this.userGroupMap.remove(user);
    }

    private Map<String, String> extractNativeHeadersFromString(MessageHeaders messageHeaders) {
        String headersString = messageHeaders.toString();

        headersString = headersString.substring(headersString.indexOf("nativeHeaders"));
        headersString = headersString.substring(headersString.indexOf("{") + 1, headersString.indexOf("]}") + 1);

        Map<String, String> nativeHeaders = new HashMap<>();

        String[] headers = headersString.split(", ");
        for (String s : headers) {
            String[] keyValue = s.split("=");
            String key = keyValue[0];
            String value = keyValue[1].substring(1, keyValue[1].length() - 1);

            nativeHeaders.put(key, value);
        }

        return nativeHeaders;
    }

    public void setGroupMovieTime(Long groupId, String time) {
        this.groupSessionMap.get(groupId).setCurrentMovieTime(time);
    }

    public void changeGroupMovieState(Long groupId, String state) {
        this.groupSessionMap.get(groupId).setMovieState(state);
    }

    public String getGroupMovieTime(Long groupId) {
        return this.groupSessionMap.get(groupId).getCurrentMovieTime();
    }

    public String getGroupMovieState(Long groupId) {
        return this.groupSessionMap.get(groupId).getMovieState();
    }
}
