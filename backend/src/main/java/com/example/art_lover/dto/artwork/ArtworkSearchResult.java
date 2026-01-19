package com.example.art_lover.dto.artwork;

import java.util.List;

public record ArtworkSearchResult(
        String title,
        String artist,
        String year,
        String movement,
        List<String> imageUrls,
        String description,
        String keywords) {
    public ArtworkSearchResult withImageUrls(List<String> newImageUrls) {

        if (newImageUrls == null || newImageUrls.isEmpty()) {
            return this;
        }

        return new ArtworkSearchResult(
                title,
                artist,
                year,
                movement,
                newImageUrls,
                description,
                keywords);
    }
}
