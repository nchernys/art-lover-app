package com.example.art_lover.dto.artwork;

public record UpdateArtworkCommand(
                String title,
                String artistId,
                String year,
                String continent,
                String country,
                Boolean bookmark,
                String movement,
                String description) {
}
