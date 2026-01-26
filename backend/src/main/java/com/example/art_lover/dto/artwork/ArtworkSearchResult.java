package com.example.art_lover.dto.artwork;

import java.util.List;
import com.example.art_lover.dto.artwork.ArtworkBoxBounds;

public record ArtworkSearchResult(
        String title,
        String artist,
        String year,
        String movement,
        List<String> imageUrls,
        String description,
        String keywords,
        Boolean bookmark,
        ArtworkBoxBounds box) {
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
                keywords,
                bookmark,
                box);
    }
}
