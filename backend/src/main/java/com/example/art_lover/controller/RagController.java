package com.example.art_lover.controller;

import org.bson.BsonArray;
import org.bson.BsonValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.art_lover.service.RAG.*;
import org.bson.Document;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final VectorSearchService verctorSearchService;
    private final EmbeddingService embeddingService;
    private final RagGeminiService ragGeminiService;

    public RagController(VectorSearchService verctorSearchService, EmbeddingService embeddingService,
            RagGeminiService ragGeminiService) {
        this.verctorSearchService = verctorSearchService;
        this.embeddingService = embeddingService;
        this.ragGeminiService = ragGeminiService;
    }

    // Query the RAG system
    @PostMapping("/query")
    public ResponseEntity<String> query(@RequestBody Map<String, String> request) {
        String question = (String) request.get("question");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Question is required");
        }

        BsonArray queryEmbedding = embeddingService.embedQuery(question);
        List<Double> queryDoubles = bsonArrayToDoubleList(queryEmbedding);
        try {
            List<Document> answer = verctorSearchService.search(queryDoubles, 5, 150);

            String context = answer.stream()
                    .map(d -> d.getString("text"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n\n"));

            String LlmResponse = ragGeminiService.generateRagResponse(question, context);
            return ResponseEntity.ok(LlmResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception: " + e.getMessage());
        }
    }

    public static List<Double> bsonArrayToDoubleList(BsonArray array) {
        return array.stream()
                .map(BsonValue::asDouble)
                .map(bsonDouble -> bsonDouble.getValue())
                .toList();
    };
}
