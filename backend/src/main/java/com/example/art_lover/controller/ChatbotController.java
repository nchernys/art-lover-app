package com.example.art_lover.controller;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.art_lover.dto.chatbot.ChatbotMessageView;
import com.example.art_lover.model.ChatbotMessageModel;
import com.example.art_lover.service.ChatbotService;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @GetMapping("/messages")
    public List<ChatbotMessageModel> fetchMessages(
            Authentication authentication) {
        return chatbotService.fetchMessages(authentication.getName());
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveMessages(
            @RequestBody ChatbotMessageModel message,
            Authentication authentication) {

        chatbotService.saveMessage(authentication.getName(), message);
        return ResponseEntity.ok("Message saved");
    }

    @DeleteMapping("/delete-all-messages")
    public ResponseEntity<String> deleteAllMessages(
            Authentication authentication) {

        chatbotService.deleteAllMessages(authentication.getName());
        return ResponseEntity.ok("Messages deleted.");
    }
}
