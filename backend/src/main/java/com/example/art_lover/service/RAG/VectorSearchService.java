package com.example.art_lover.service.RAG;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.search.FieldSearchPath;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.vectorSearch;
import static com.mongodb.client.model.search.SearchPath.fieldPath;
import static com.mongodb.client.model.search.VectorSearchOptions.approximateVectorSearchOptions;

@Service
public class VectorSearchService {

    private final MongoCollection<Document> collection;

    public VectorSearchService(
            MongoClient mongoClient,
            @Value("${mongodb.database-name}") String databaseName,
            @Value("${mongodb.collection-name}") String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection(collectionName);
    };

    public List<Document> search(
            List<Double> queryVector,
            int limit,
            int numCandidates) {

        String indexName = "vector_index";
        FieldSearchPath fieldPath = fieldPath("embedding");

        List<Bson> pipeline = List.of(
                vectorSearch(
                        fieldPath,
                        queryVector,
                        indexName,
                        limit,
                        approximateVectorSearchOptions(numCandidates)));

        List<Document> result = collection.aggregate(pipeline).into(new ArrayList<>());

        return result;
    }
}
