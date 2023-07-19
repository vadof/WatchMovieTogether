package com.server.backend.services;

import com.server.backend.entity.Chat;
import com.server.backend.entity.Message;
import com.server.backend.entity.MessageType;
import com.server.backend.entity.User;
import com.server.backend.repository.ChatRepository;
import com.server.backend.repository.MessageRepository;
import com.server.backend.repository.UserRepository;
import com.server.backend.websocket.WebSocketController;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate template;

    public Message addMessageToChat(Long chatId, String message, String username) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        User user = userRepository.findByUsername(username).orElseThrow();

        Message message1 = Message.builder()
                .message(message)
                .messageType(MessageType.USER)
                .user(user)
                .chat(chat).build();

        messageRepository.save(message1);

        chat.getMessages().add(message1);
        chatRepository.save(chat);

        template.convertAndSend("/group/" + 1 + "/chat", message1);

        return message1;
    }

    public Message addSystemMessageToChat(Long chatId, String message) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        Message systemMessage = Message.builder()
                .message(message)
                .messageType(MessageType.SYSTEM)
                .chat(chat).build();

        messageRepository.save(systemMessage);

        chat.getMessages().add(systemMessage);
        chatRepository.save(chat);

        return systemMessage;
    }

    public String generateMovieChangeMessage(String movieName, String selectedTranslation) {
        return String.format("Selected a new movie \"%s\" translated by \"%s\"",
                movieName, selectedTranslation);
    }

    public String generateUserLeaveMessage(String username) {
        return String.format("%s left the group", username);
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
