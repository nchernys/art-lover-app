package com.example.art_lover.controller;

import com.example.art_lover.dto.rag.*;
import com.example.art_lover.model.RagChunk;
import com.example.art_lover.service.EmbeddingService;
import com.example.art_lover.service.GeminiRagService;
import com.example.art_lover.service.RagSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/chat/rag")
public class RagController {

    private final EmbeddingService embeddingService;
    private final RagSearchService ragSearchService;
    private final GeminiRagService geminiRagService;

    public RagController(
            EmbeddingService embeddingService,
            RagSearchService ragSearchService,
            GeminiRagService geminiRagService) {
        this.embeddingService = embeddingService;
        this.ragSearchService = ragSearchService;
        this.geminiRagService = geminiRagService;
    }

    @PostMapping
    public RagResponse chat(@RequestBody RagRequest request) {

        // 1. Embed the question
        List<Float> embedding = embeddingService.embed(request.question());

        List<Double> queryVector = embedding.stream().map(Float::doubleValue).toList();

        // 2. Vector search
        List<RagChunk> chunks = ragSearchService.searchByVector(queryVector);

        // 3. Gemini RAG answer
        String answer = geminiRagService.answer(request.question(), chunks);

        return new RagResponse(answer);
    }
}
