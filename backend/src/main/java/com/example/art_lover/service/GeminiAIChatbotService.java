package com.example.art_lover.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.example.art_lover.model.ChatbotMessageModel;

@Service
public class GeminiAIChatbotService {

    private final Client client;

    public GeminiAIChatbotService(Client client) {
        this.client = client;
    }

    public String generateChatResponse(
            String userPrompt, List<ChatbotMessageModel> prevMessages) {

        String history = prevMessages.stream()
                .map(m -> m.getRole() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));

        String prompt = String.format(
                """
                        Read all previous messages in the conversation: %s.
                        Write a 30-50 word chat response based on the user request: %s.
                        Write like an art historian. Use general well-known art history knowledge. Do not quote museum or Wikipedia text.

                        Rules:
                        - When the user requests a fun fact, do not repeat any fun facts already mentioned in the current conversation.

                        - If the user makes consecutive fun fact requests about the same artist, artwork, or movement, rotate the focus of each response:
                            - First fun fact: the artist
                            - Second fun fact: the artwork
                            - Third fun fact: the movement
                            - Continue cycling this pattern for subsequent requests

                        """,
                history,
                userPrompt);

        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null);

        return response.text();
    }

}
