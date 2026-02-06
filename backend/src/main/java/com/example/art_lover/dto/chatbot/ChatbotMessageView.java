package com.example.art_lover.dto.chatbot;

import java.time.Instant;

public record ChatbotMessageView(
        String role,
        String content,
        String userId,
        Instant createdAt) {

}
