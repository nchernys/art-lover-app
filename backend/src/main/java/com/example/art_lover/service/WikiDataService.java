package com.example.art_lover.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class WikiDataService {

    private static final String ENDPOINT = "https://query.wikidata.org/sparql";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * @return image URL (Commons Special:FilePath) or null
     */
    public String findImage(String title, String artist) throws Exception {

        String sparql = """
                SELECT ?image WHERE {
                  ?artwork wdt:P31 wd:Q3305213;
                           wdt:P170 wd:Q5598;
                           wdt:P18 ?image.
                }
                LIMIT 1
                """.formatted(artist, title);

        String url = ENDPOINT +
                "?format=json&query=" +
                URLEncoder.encode(sparql, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/sparql+json")
                .header("User-Agent", "ArtLoverApp/1.0 (contact: dev@example.com)")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode bindings = mapper.readTree(response.body())
                .path("results")
                .path("bindings");

        System.out.println("HERE!!!!!!!   " + bindings);
        if (!bindings.isArray() || bindings.isEmpty()) {
            return null;
        }

        JsonNode imageNode = bindings.get(0).path("image");
        if (imageNode.isMissingNode()) {
            return null;
        }
        System.out.println("NODE   " + imageNode.path("value").asText());
        // Example:
        // https://commons.wikimedia.org/wiki/Special:FilePath/Rembrandt_...
        return imageNode.path("value").asText();
    }
}
