package com.example.art_lover.service;

import java.util.Objects;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.art_lover.model.ArtworkModel;
import com.example.art_lover.dto.ArtworkSearchResult;
import com.example.art_lover.metmuseum.dto.MetObjectResponse;
import com.example.art_lover.metmuseum.dto.MetSearchResponse;

@Service
public class MetMuseumService {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<ArtworkSearchResult> search(String keyword) {
        String searchUrl = "https://collectionapi.metmuseum.org/public/collection/v1/search?q=" + keyword;

        MetSearchResponse searchResponse = restTemplate.getForObject(searchUrl, MetSearchResponse.class);

        if (searchResponse == null || searchResponse.objectIDs() == null) {
            return List.of();
        }

        // Take the first few object IDs to avoid hammering the API
        return searchResponse.objectIDs().stream()
                .limit(10)
                .map(this::fetchArtworkById)
                .filter(Objects::nonNull)
                .toList();
    }

    // Fetch full artwork data for a single object ID
    private ArtworkSearchResult fetchArtworkById(Integer objectId) {

        // URL for a single artwork object
        String objectUrl = "https://collectionapi.metmuseum.org/public/collection/v1/objects/" + objectId;

        // Call the Met Museum object endpoint
        MetObjectResponse obj = restTemplate.getForObject(objectUrl, MetObjectResponse.class);

        // If no image is available, ignore this result
        if (obj == null || obj.primaryImage() == null) {
            return null;
        }

        System.out.println("IMAGES" + obj.title() + obj.primaryImage());

        // Map MET response to the DTO for search results
        return new ArtworkSearchResult(
                obj.title(),
                obj.artistDisplayName(),
                obj.objectDate(),
                obj.period(),
                obj.primaryImage() == null
                        ? List.of()
                        : List.of(obj.primaryImage()),
                obj.keywords(),
                "MET Museum");
    }
}
