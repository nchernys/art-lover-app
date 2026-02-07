package com.example.art_lover.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.art_lover.dto.chatbot.ChatbotMessageView;
import com.example.art_lover.exceptions.ForbiddenOperationException;
import com.example.art_lover.repository.ChatbotRepository;
import com.example.art_lover.model.ChatbotMessageModel;
import com.example.art_lover.service.GeminiAIChatbotService;

@Service
public class ChatbotService {

    private final ChatbotRepository chatbotRepository;
    private final GeminiAIChatbotService geminiAIChatbotService;

    public ChatbotService(ChatbotRepository chatbotRepository, GeminiAIChatbotService geminiAIChatbotService) {
        this.chatbotRepository = chatbotRepository;
        this.geminiAIChatbotService = geminiAIChatbotService;
    }

    public List<ChatbotMessageModel> fetchMessages(String userId) {
        return chatbotRepository.findAllByUserId(userId);
    }

    public void saveMessage(String userId, ChatbotMessageModel message) {
        if (!Objects.equals(message.getUserId(), userId))
            throw new ForbiddenOperationException("User is not authorized to save a new message.");
        chatbotRepository.save(message);
        String userPrompt = message.getContent();
        List<ChatbotMessageModel> prevMessages = chatbotRepository.findAllByUserId(userId);
        String chatbotResponse = geminiAIChatbotService.generateChatResponse(userPrompt, prevMessages);
        ChatbotMessageModel responseMessage = new ChatbotMessageModel();
        responseMessage.setRole("assistant");
        responseMessage.setUserId(userId);
        responseMessage.setContent(chatbotResponse);
        chatbotRepository.save(responseMessage);
    }

    public void deleteAllMessages(String userId) {
        chatbotRepository.deleteAllByUserId(userId);
    }
}
