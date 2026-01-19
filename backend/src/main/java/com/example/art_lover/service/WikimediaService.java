package com.example.art_lover.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WikimediaService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<String> findArtworkImage(String keywords) {

        List<String> results = new ArrayList<>();

        try {
            List<String> images = searchArtworkImages(keywords);

            for (String image : images) {
                results.add(image);
            }

            if (images.isEmpty()) {
                System.out.println("NO IMAGES FOUND");
            } else {
                System.out.println("IMAGES FOUND: " + images.size());
            }

        } catch (Exception e) {
            System.out.println("ERROR DURING COMMONS IMAGE SEARCH");
            e.printStackTrace();
        }

        return results;
    }

    private List<String> searchArtworkImages(String keywords)
            throws Exception {

        System.out.println("Using raw keywords: " + keywords);

        String url = UriComponentsBuilder
                .fromUriString("https://commons.wikimedia.org/w/api.php")
                .queryParam("action", "query")
                .queryParam("format", "json")
                .queryParam("generator", "search")
                .queryParam("gsrsearch", keywords)
                .queryParam("gsrnamespace", 6) // FILE namespace
                .queryParam("gsrlimit", 10)
                .queryParam("prop", "imageinfo")
                .queryParam("iiprop", "url|size|mime|mediatype")
                .build(false)
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                withUserAgent(),
                String.class);

        JsonNode pagesNode = mapper.readTree(response.getBody())
                .path("query")
                .path("pages");

        List<String> images = new ArrayList<>();

        if (!pagesNode.isObject()) {
            System.out.println("Commons returned NO pages");
            return images;
        }

        Iterator<String> fieldNames = pagesNode.fieldNames();
        while (fieldNames.hasNext()) {
            String key = fieldNames.next();
            JsonNode page = pagesNode.get(key);
            JsonNode imageInfo = page.path("imageinfo");

            if (!imageInfo.isArray() || imageInfo.isEmpty()) {
                continue;
            }

            JsonNode info = imageInfo.get(0);

            String image = info.path("url").asText();

            images.add(image);

            System.out.println("Image: " + image);
        }

        return images;
    }

    private HttpEntity<Void> withUserAgent() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, "ArtLoverApp/1.0");
        return new HttpEntity<>(headers);
    }
}
