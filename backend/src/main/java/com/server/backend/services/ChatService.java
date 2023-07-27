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
                .chat(chat).build();

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
                .chat(chat).build();

        messageRepository.save(systemMessage);

        chat.getMessages().add(systemMessage);
        chatRepository.save(chat);

        webSocketService.sendObjectByWebsocket("/group/" + groupId + "/chat", systemMessage);
    }

    public String generateMovieChangeMessage(String movieName, String selectedTranslation) {
        return String.format("Selected a new movie \"%s\" translated by \"%s\"",
                movieName, selectedTranslation);
    }

    public String generateUserLeaveMessage(String username) {
        return String.format("%s left the group", username);
    }

    public String generateUserKickedMessage(String kickedUserUsername, String whoKickedUsername) {
        return String.format("%s was kicked by %s from the group",
                kickedUserUsername, whoKickedUsername);
    }

    public String generateUserJoinMessage(String username) {
        return String.format("%s joined the group", username);
    }

    public String generateTranslationChangeMessage(String translationName) {
        return String.format("\"%s\" was chosen as the translation", translationName);
    }

    public String generateGroupCreateMessage(String userUsername) {
        return String.format("%s created a group", userUsername);
    }
}
