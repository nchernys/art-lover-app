package com.example.art_lover.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.genai.Client;
import com.google.genai.types.Blob;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.example.art_lover.dto.artwork.ArtworkSearchResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiAILImageRecognitionService {

    private final Client client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MuseumArtworkService museumImageSearchService;
    private final WikimediaService wikimediaService;

    public GeminiAILImageRecognitionService(Client client, MuseumArtworkService museumImageSearchService,
            WikimediaService wikimediaService) {
        this.client = client;
        this.museumImageSearchService = museumImageSearchService;
        this.wikimediaService = wikimediaService;
    }

    public List<ArtworkSearchResult> recognizeImage(MultipartFile image) throws IOException {

        byte[] imageBytes = image.getBytes();

        Blob blob = Blob.builder()
                .mimeType(image.getContentType())
                .data(imageBytes)
                .build();

        Content content = Content.builder()
                .parts(List.of(
                        Part.builder().inlineData(blob).build(),
                        Part.builder()
                                .text("""
                                        You are an expert in art history, architecture, and visual culture.

                                            Task:
                                            Analyze the image and identify ALL real, catalogued subjects that apply.

                                            A valid subject is one that:
                                            - Exists as a named work, structure, or photograph
                                            - Is documented or referenceable in at least one of:
                                            wikipedia, museums, archives, architectural records, scholarly sources,
                                            exhibition catalogues, or historical documentation.

                                            Recognize this artwork and propose 2-3 possible options. Describe who is the artist or an architect, the title of the artwork, year or period created, art movement.

                                            Generate an original 120-150 word description of the artwork as an art historian, focusing on its artistic and stylistic qualities and significance in art history. Do not copy text from Wikipedia or museum or websites verbatim.
                                        Respond ONLY with valid JSON in the following format:

                                            [
                                                {

                                                    "title": "string",
                                                    "artist": "string",
                                                    "year": "string",
                                                    "movement": "string",
                                                    "description": "string",
                                                    "keywords": "string"
                                                }
                                            ]

                                            Rules:
                                            - if unknown, use "Unknown"
                                            - Keywords must include up to three distinctive words from the name the artist is most known for and the artwork title (e.g.: repin  rembrandt girl starry self night). Don't add commas between keywords.
                                            - Keywords must not include filler words such as with, and, the, of and hyphens -.
                                            - do not include explanations or extra text
                                            - do NOT reinterpret casual or tourist photos as artworks.
                                            - If the image is a casual photo but depicts a real, named building or structure,
                                                               return the building or structure.
                                            - if the image matches a real photographic work or series, return a photograph.

                                        Return ONLY raw JSON.
                                        Do NOT use markdown.
                                        Do NOT wrap in ```json blocks.
                                        """)
                                .build()))
                .build();

        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                List.of(content),
                GenerateContentConfig.builder().build());

        String json = response.text();

        List<ArtworkSearchResult> results = objectMapper.readValue(
                json,
                new TypeReference<List<ArtworkSearchResult>>() {
                });

        List<ArtworkSearchResult> enriched = new ArrayList<>();

        if (!results.isEmpty()) {
            try {

                for (ArtworkSearchResult result : results) {
                    String url = museumImageSearchService.findArtworkInMuseums(result.title(), result.artist());
                    List<String> urls = wikimediaService.findArtworkImage(result.keywords());
                    List<String> totalImageUrls = new ArrayList<>();

                    if (url != null && !url.isBlank()) {
                        totalImageUrls.add(url);
                    }
                    if (urls != null && !urls.isEmpty()) {
                        totalImageUrls.addAll(urls);
                    }
                    if (totalImageUrls != null && !totalImageUrls.isEmpty()) {
                        enriched.add(result.withImageUrls(totalImageUrls));
                    } else {
                        enriched.add(result);
                    }

                }
            } catch (Exception e) {
                throw new IOException("Failed to search image URLs", e);
            }
        }

        return enriched;
    }

}
