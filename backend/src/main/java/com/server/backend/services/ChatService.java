package com.server.backend.services;

import com.server.backend.entity.Chat;
import com.server.backend.entity.Message;
import com.server.backend.entity.User;
import com.server.backend.repository.ChatRepository;
import com.server.backend.repository.MessageRepository;
import com.server.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public Message addMessageToChat(Long chatId, String message, String username) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        User user = userRepository.findByUsername(username).orElseThrow();

        Message message1 = new Message();
        message1.setMessage(message);
        message1.setUser(user);
        message1.setChat(chat);

        messageRepository.save(message1);

        chat.getMessages().add(message1);
        chatRepository.save(chat);

        return message1;
    }
}
