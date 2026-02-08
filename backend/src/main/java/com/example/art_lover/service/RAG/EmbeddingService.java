package com.example.art_lover.service.RAG;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.bson.BsonArray;
import org.bson.BsonDouble;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.time.Duration.ofSeconds;

@Service
public class EmbeddingService {

        private final OpenAiEmbeddingModel embeddingModel;

        public EmbeddingService(
                        @Value("${openai.api-key}") String apiKey,
                        @Value("${openai.embedding-model:text-embedding-3-small}") String modelName) {
                this.embeddingModel = OpenAiEmbeddingModel.builder()
                                .apiKey(apiKey)
                                .modelName(modelName)
                                .timeout(ofSeconds(60))
                                .build();
        }

        // Generates embeddings for multiple texts (used for indexing / ingestion).
        public List<BsonArray> embedAll(List<String> texts) {

                List<TextSegment> segments = texts.stream()
                                .map(TextSegment::from)
                                .toList();

                Response<List<Embedding>> response = embeddingModel.embedAll(segments);

                return response.content().stream()
                                .map(embedding -> new BsonArray(
                                                embedding.vectorAsList().stream()
                                                                .map(BsonDouble::new)
                                                                .toList()))
                                .toList();
        }

        // Generates an embedding for a single query (used for vector search).
        public BsonArray embedQuery(String text) {

                Response<Embedding> response = embeddingModel.embed(text);

                return new BsonArray(
                                response.content().vectorAsList().stream()
                                                .map(BsonDouble::new)
                                                .toList());
        }
}
