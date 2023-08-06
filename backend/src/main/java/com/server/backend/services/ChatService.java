package com.server.backend.services;

import com.server.backend.entity.Chat;
import com.server.backend.entity.Message;
import com.server.backend.entity.MessageType;
import com.server.backend.entity.User;
import com.server.backend.repository.ChatRepository;
import com.server.backend.repository.GroupRepository;
import com.server.backend.repository.MessageRepository;
import com.server.backend.repository.UserRepository;
import com.server.backend.websocket.WebSocketService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final GroupRepository groupRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;

    public Message addMessageToGroupChat(Long groupId, String message, String username) {
        Chat chat = groupRepository.findById(groupId).orElseThrow().getChat();
        User user = userRepository.findByUsername(username).orElseThrow();

        Message message1 = Message.builder()
                .message(message)
                .messageType(MessageType.USER)
                .user(user)
                .build();

        messageRepository.save(message1);

        chat.getMessages().add(message1);
        chatRepository.save(chat);

        return message1;
    }

    public void addSystemMessageToGroupChat(Long groupId, String message) {
        Chat chat = groupRepository.findById(groupId).orElseThrow().getChat();
        Message systemMessage = Message.builder()
                .message(message)
                .messageType(MessageType.SYSTEM)
                .build();

        messageRepository.save(systemMessage);

        chat.getMessages().add(systemMessage);
        chatRepository.save(chat);

        webSocketService.sendObjectByWebsocket("/group/" + groupId + "/chat", systemMessage);
    }

    public void sendGroupCreateMessage(Long groupId, String userUsername) {
        this.addSystemMessageToGroupChat(groupId, String.format("%s created a group", userUsername));
    }

    public void sendUserLeaveMessage(Long groupId, String username) {
        this.addSystemMessageToGroupChat(groupId, String.format("%s left the group", username));
    }

    public void sendUserKickedMessage(Long groupId, String kickedUserUsername, String whoKickedUsername) {
        this.addSystemMessageToGroupChat(groupId,
                String.format("%s was kicked by %s from the group",
                        kickedUserUsername, whoKickedUsername));
    }

    public void sendUserJoinMessage(Long groupId, String username) {
        this.addSystemMessageToGroupChat(groupId, String.format("%s joined the group", username));
    }

    public void sendMovieChangeMessage(Long groupId, String movieName, String translationName) {
        this.addSystemMessageToGroupChat(groupId,
                String.format("Selected a new movie \"%s\" translated by \"%s\"",
                        movieName, translationName));
    }

    public void sendTranslationChangeMessage(Long groupId, String translationName) {
        this.addSystemMessageToGroupChat(groupId,
                String.format("\"%s\" was chosen as the translation", translationName));
    }

    public void sendSeriesChangeMesasge(Long groupId, String seriesName, String translationName) {
        this.addSystemMessageToGroupChat(groupId,
                String.format("Selected a new series \"%s\" translated by \"%s\"",
                        seriesName, translationName));
    }

    public void sendSeriesTranslationChangeMessage(Long groupId, String translationName) {
        this.addSystemMessageToGroupChat(groupId,
                String.format("\"%s\" was chosen as the translation", translationName));
    }

    public void sendSeriesEpisodeChange(Long groupId, String season, String episode) {
        this.addSystemMessageToGroupChat(groupId,
                String.format("Selected %s episode of %s season", episode, season));
    }
}
