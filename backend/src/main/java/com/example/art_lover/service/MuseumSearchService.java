package com.example.art_lover.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.art_lover.config.RestTemplateConfig;

@Service
public class MuseumSearchService {

    @Value("${smithsonian.api.key}")
    private String smithsonianApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public MuseumSearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Search multiple museum APIs for exact artwork
     */
    public String findArtworkInMuseums(String title, String artist) {

        // 1. Metropolitan Museum
        String metUrl = searchMetMuseum(title, artist);
        if (metUrl != null)
            return metUrl;

        // 2. Art Institute of Chicago
        String articUrl = searchArtic(title, artist);
        if (articUrl != null)
            return articUrl;

        // 3. Rijksmuseum
        String rijksUrl = searchRijksmuseum(title, artist);
        if (rijksUrl != null)
            return rijksUrl;

        // 4. Smithsonian
        String smithsonianUrl = searchSmithsonian(title, artist);
        if (smithsonianUrl != null)
            return smithsonianUrl;

        return null;
    }

    private String searchMetMuseum(String title, String artist) {
        try {
            String query = title + " " + artist;
            String searchUrl = "https://collectionapi.metmuseum.org/public/collection/v1/search?hasImages=true&q="
                    + URLEncoder.encode(query, StandardCharsets.UTF_8);

            String response = restTemplate.getForObject(searchUrl, String.class);
            JsonNode root = mapper.readTree(response);

            JsonNode objectIDs = root.path("objectIDs");
            if (objectIDs.size() > 0) {
                int objectId = objectIDs.get(0).asInt();

                String objectUrl = "https://collectionapi.metmuseum.org/public/collection/v1/objects/" + objectId;
                JsonNode object = mapper.readTree(restTemplate.getForObject(objectUrl, String.class));

                String primaryImage = object.path("primaryImage").asText();

                // Verify it's the right artwork by checking title match
                String foundTitle = object.path("title").asText();
                if (foundTitle.toLowerCase().contains(title.toLowerCase())) {
                    return primaryImage.isEmpty() ? null : primaryImage;
                }
            }
        } catch (Exception e) {
            System.err.println("Met search failed: " + e.getMessage());
        }

        return null;
    }

    private String searchArtic(String title, String artist) {
        try {
            String query = title + " " + artist;
            String url = "https://api.artic.edu/api/v1/artworks/search?q=" +
                    URLEncoder.encode(query, StandardCharsets.UTF_8) +
                    "&limit=5&fields=id,title,image_id,artist_display";

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);
            JsonNode data = root.path("data");

            // Find best match
            for (JsonNode artwork : data) {
                String foundTitle = artwork.path("title").asText();
                if (foundTitle.toLowerCase().contains(title.toLowerCase())) {
                    String imageId = artwork.path("image_id").asText();
                    if (!imageId.isEmpty()) {
                        return String.format(
                                "https://www.artic.edu/iiif/2/%s/full/843,/0/default.jpg",
                                imageId);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Artic search failed: " + e.getMessage());
        }

        return null;
    }

    private String searchRijksmuseum(String title, String artist) {
        try {
            String url = "https://data.rijksmuseum.nl/search/collection?title=" + title + "&creator=" + artist;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);
            JsonNode data = root.path("data");
            System.out.println("RIJ  ....  " + data);

            // Find best match
            for (JsonNode artwork : data) {
                String foundTitle = artwork.path("title").asText();
                if (foundTitle.toLowerCase().contains(title.toLowerCase())) {
                    String imageId = artwork.path("image_id").asText();
                    if (!imageId.isEmpty()) {
                        return String.format(
                                "https://iiif.micr.io/RFwqO/full/max/0/default.png",
                                imageId);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Rijksmuseum search failed: " + e.getMessage());
        }

        return null;
    }

    private String searchSmithsonian(String title, String artist) {
        try {
            String query = artist + " " + title;
            String url = UriComponentsBuilder
                    .fromUriString("https://api.si.edu/openaccess/api/v1.0/search")
                    .queryParam("q", query)
                    .queryParam("api_key", smithsonianApiKey)
                    .toUriString();
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);
            JsonNode data = root.path("data");
            System.out.println("RIJ  ....  " + data);

            // Find best match
            for (JsonNode item : data) {
                JsonNode media = item
                        .path("content")
                        .path("descriptiveNonRepeating")
                        .path("online_media")
                        .path("media");

                if (media.isArray() && media.size() > 0) {
                    String imageUrl = media.get(0).path("content").asText();
                    if (!imageUrl.isEmpty()) {
                        return imageUrl;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Smithsonian search failed: " + e.getMessage());
        }

        return null;
    }
}