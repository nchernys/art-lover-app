package com.example.art_lover.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class RagEmbeddingService {

    private final EmbeddingModel embeddingModel;

    public RagEmbeddingService(@Value("${openai.api.key}") String apiKey) {
        this.embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("text-embedding-3-small") // or "text-embedding-3-large" for better quality
                .timeout(Duration.ofSeconds(60))
                .maxRetries(3)
                .build();
    }

    public List<Float> embed(String text) {
        Response<Embedding> response = embeddingModel.embed(text);
        Embedding embedding = response.content();
        float[] vector = embedding.vector();

        List<Float> result = new ArrayList<>(vector.length);
        for (float v : vector) {
            result.add(v);
        }
        return result;
    }

    /**
     * Exposes the embedding model for use in RAG service
     */
    public EmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }
}