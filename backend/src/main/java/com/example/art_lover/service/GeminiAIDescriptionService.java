package com.example.art_lover.service;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@Service
public class GeminiAIDescriptionService {
    
    private final Client client;

    public GeminiAIDescriptionService(Client client) {
            this.client = client;
        }

    public String generateDescription(
           String userPrompt
    ) {

        String prompt = String.format(
            """
            Write an original 120–150 word description of the artwork based on the user request: %s.
            Include the artist name, artwork title, artwork year, significance in the art history.
            Write like an art historian. Use general art history knowledge. Do not quote museum or Wikipedia text.
            """,
            userPrompt
        );
        
        GenerateContentResponse response =
            client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null
            );

        return response.text();
    }

}
