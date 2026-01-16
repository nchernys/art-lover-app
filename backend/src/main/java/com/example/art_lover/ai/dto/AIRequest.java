package com.example.art_lover.ai.dto;

import java.util.List;

public record AIRequest(
    List<Content> contents
) {
    public record Content(List<Part> parts) {}
    public record Part(String text) {}
}