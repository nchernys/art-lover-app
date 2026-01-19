package com.example.art_lover.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EuropeanaService {

    @Value("${europeana.base-url}")
    private String baseUrl;

    @Value("${europeana.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getImageUrl(String artist, String artwork) {
        String query = artist + " " + artwork;

        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/search.json")
                .queryParam("query", query)
                .queryParam("media", "true")
                .queryParam("rows", 1)
                .queryParam("wskey", apiKey)
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("items");

            if (items.isArray() && items.size() > 0) {
                JsonNode previews = items.get(0).path("edmPreview");
                if (previews.isArray() && previews.size() > 0) {
                    System.out.println("URL TO RETURN " + previews.get(0).asText());
                    return previews.get(0).asText();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Europeana response", e);
        }

        return null;
    }
}
