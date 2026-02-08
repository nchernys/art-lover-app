package com.example.art_lover.service.RAG;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@Service
public class RagGeminiService {

        private final Client client;

        public RagGeminiService(Client client) {
                this.client = client;
        }

        // context is the info from the vector db
        public String generateRagResponse(
                        String userPrompt, String context) {

                String prompt = String.format(
                                """
                                                Write a response to the user's query: %s.
                                                For your response use the context provided: %s.
                                                """,
                                userPrompt, context);

                GenerateContentResponse response = client.models.generateContent(
                                "gemini-2.5-flash",
                                prompt,
                                null);

                return response.text();
        }

}
