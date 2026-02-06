package com.example.art_lover.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.art_lover.dto.chatbot.ChatbotMessageView;
import com.example.art_lover.exceptions.ForbiddenOperationException;
import com.example.art_lover.repository.ChatbotRepository;
import com.example.art_lover.model.ChatbotMessageModel;

@Service
public class ChatbotService {

    private final ChatbotRepository chatbotRepository;

    public ChatbotService(ChatbotRepository chatbotRepository) {
        this.chatbotRepository = chatbotRepository;
    }

    public List<ChatbotMessageModel> fetchMessages(String userId) {
        return chatbotRepository.findAllByUserId(userId);
    }

    public void saveMessage(String userId, ChatbotMessageModel message) {
        if (!Objects.equals(message.getUserId(), userId))
            throw new ForbiddenOperationException("User is not authorized to save a new message.");
        chatbotRepository.save(message);
    }
}
