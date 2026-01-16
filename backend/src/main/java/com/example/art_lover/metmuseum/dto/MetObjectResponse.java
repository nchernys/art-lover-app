package com.example.art_lover.metmuseum.dto;

public record MetObjectResponse(
        String title,
        String artistDisplayName,
        String objectDate,
        String period,
        String primaryImage,
        String keywords) {
}
