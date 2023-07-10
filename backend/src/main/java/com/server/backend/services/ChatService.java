package com.server.backend.services;

import com.server.backend.entity.Chat;
import com.server.backend.entity.Message;
import com.server.backend.entity.User;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.ChatRepository;
import com.server.backend.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final JwtService jwtService;
    private final MessageRepository messageRepository;

    public void addMessageToChat(Long chatId, String message, String token) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        User user = jwtService.getUserFromBearerToken(token).orElseThrow();

        Message message1 = new com.server.backend.entity.Message();
        message1.setMessage(message);
        message1.setUser(user);
        message1.setChat(chat);

        messageRepository.save(message1);

        chat.getMessages().add(message1);
        chatRepository.save(chat);
    }
}
