package com.example.art_lover.dto.artwork;

import java.util.List;
import com.example.art_lover.dto.artwork.ImageBoxBounds;

public record ArtworkSearchHit(
        String title,
        String artist,
        String year,
        String movement,
        List<String> imageUrls,
        String description,
        String keywords,
        Boolean bookmark,
        ImageBoxBounds box) {
    public ArtworkSearchHit withImageUrls(List<String> newImageUrls) {

        if (newImageUrls == null || newImageUrls.isEmpty()) {
            return this;
        }

        return new ArtworkSearchHit(
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
