package com.example.art_lover.dto.artwork;

public record CreateArtworkCommand(String title,
                String artist,
                String artistId,
                String year,
                String movement,
                String imageUrl,
                String imageKey,
                String previewKey,
                String description,
                Boolean bookmark,
                String box) {

}
