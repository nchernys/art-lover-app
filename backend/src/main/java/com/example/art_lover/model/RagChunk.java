package com.example.art_lover.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rag_data")
public class RagChunk {
    private String text;
    private List<Double> embedding;
    private String source;
    private int chunkId;

    public String getText() {
        return text;
    }

    public List<Double> getEmbedding() {
        return embedding;
    }

    public String getSource() {
        return source;
    }

    public int getChunkId() {
        return chunkId;
    }
}