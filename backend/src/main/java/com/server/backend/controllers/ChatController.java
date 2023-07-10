package com.server.backend.controllers;

import com.server.backend.services.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/{id}")
    public void addMessageToChat(@PathVariable Long id, @RequestBody String message,
                                 @RequestHeader("Authorization") String token) {
        this.chatService.addMessageToChat(id, message, token);
    }

}
