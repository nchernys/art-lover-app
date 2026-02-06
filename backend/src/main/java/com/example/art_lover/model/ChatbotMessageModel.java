package com.example.art_lover.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Document(collection = "messages")
public class ChatbotMessageModel {

    @Id
    private String id;

    private String role;
    private String content;
    private String conversationId;
    private String userId;

    @CreatedDate
    private Instant createdAt;

    public ChatbotMessageModel() {
        // required for frameworks
    }

    public ChatbotMessageModel(String id, String role, String content, String userId, Instant createdAt) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
