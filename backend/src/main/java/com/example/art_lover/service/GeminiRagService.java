package com.example.art_lover.service;

import com.example.art_lover.model.RagChunk;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GeminiRagService {

    private static final String PROJECT_ID = "gen-lang-client-0213855665";
    private static final String LOCATION = "us-central1";
    private static final String MODEL_NAME = "gemini-1.5-pro";

    private final VertexAI vertexAi;
    private final GenerativeModel model;

    public GeminiRagService() {
        this.vertexAi = new VertexAI(PROJECT_ID, LOCATION);
        this.model = new GenerativeModel(MODEL_NAME, vertexAi);
    }

    public String answer(String question, List<RagChunk> chunks) {

        StringBuilder context = new StringBuilder();
        for (RagChunk chunk : chunks) {
            context.append(chunk.getText()).append("\n\n");
        }

        String prompt = """
                You are an assistant answering strictly from the provided context.
                If the answer is not present in the context, say "I do not know".

                Context:
                %s

                Question:
                %s

                Answer:
                """.formatted(context.toString(), question);

        try {
            var response = model.generateContent(prompt);

            return response
                    .getCandidates(0)
                    .getContent()
                    .getParts(0)
                    .getText();

        } catch (IOException e) {
            throw new RuntimeException("Gemini generation failed", e);
        }
    }
}
