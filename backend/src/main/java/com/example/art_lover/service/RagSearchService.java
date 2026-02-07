package com.example.art_lover.service;

import com.example.art_lover.model.RagChunk;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagSearchService {

    private final MongoTemplate mongoTemplate;

    public RagSearchService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<RagChunk> searchByVector(List<Double> queryVector) {

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.stage("""
                        {
                          $vectorSearch: {
                            index: "embedding_index",
                            path: "embedding",
                            queryVector: %s,
                            numCandidates: 100,
                            limit: 5
                          }
                        }
                        """.formatted(queryVector)));

        return mongoTemplate
                .aggregate(agg, "rag_data", RagChunk.class)
                .getMappedResults();
    }
}
