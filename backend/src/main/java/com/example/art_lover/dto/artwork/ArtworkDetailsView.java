package com.example.art_lover.dto.artwork;

public record ArtworkDetailsView(
                String id,
                String title,
                String artist,
                String year,
                String movement,
                String imageUrl,
                String imageKey,
                String previewKey,
                Boolean bookmark,
                String description) {
}
