package com.example.art_lover.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Blob;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.example.art_lover.dto.artwork.ArtworkBoxBounds;
import com.example.art_lover.dto.artwork.ArtworkSearchResult;

@Service
public class GeminiAIImageRecognitionService {

        private final Client client;
        private final MuseumArtworkService museumImageSearchService;
        private final WikimediaService wikimediaService;
        private final ObjectMapper objectMapper = new ObjectMapper();

        public GeminiAIImageRecognitionService(
                        Client client,
                        MuseumArtworkService museumImageSearchService,
                        WikimediaService wikimediaService) {

                this.client = client;
                this.museumImageSearchService = museumImageSearchService;
                this.wikimediaService = wikimediaService;
        }

        public List<ArtworkSearchResult> recognizeImage(MultipartFile image)
                        throws IOException {

                Content content = buildImageContent(image);
                return runGeminiAndEnrich(content);
        }

        public List<ArtworkSearchResult> recognizeKeywords(String keywords)
                        throws IOException {

                Content content = buildKeywordContent(keywords);
                return runGeminiAndEnrich(content);
        }

        public ArtworkBoxBounds identifyBoxBounds(InputStream inputStream, String mimetype)
                        throws IOException {

                Content content = buildBytesImageContent(inputStream, mimetype);
                return runGeminiAndGetBoxBounds(content);
        }

        // pipeline
        private List<ArtworkSearchResult> runGeminiAndEnrich(Content content)
                        throws IOException {

                GenerateContentResponse response = client.models.generateContent(
                                "gemini-2.5-flash",
                                List.of(content),
                                GenerateContentConfig.builder().build());

                String json = response.text();

                List<ArtworkSearchResult> results = objectMapper.readValue(
                                json,
                                new TypeReference<List<ArtworkSearchResult>>() {
                                });

                if (results.isEmpty()) {
                        return results;
                }

                List<ArtworkSearchResult> enriched = new ArrayList<>();

                try {
                        for (ArtworkSearchResult result : results) {

                                String museumUrl = museumImageSearchService.findArtworkInMuseums(
                                                result.title(), result.artist());

                                List<String> wikiUrls = wikimediaService.findArtworkImage(result.keywords());

                                List<String> allUrls = new ArrayList<>();

                                if (museumUrl != null && !museumUrl.isBlank()) {
                                        allUrls.add(museumUrl);
                                }
                                if (wikiUrls != null && !wikiUrls.isEmpty()) {
                                        allUrls.addAll(wikiUrls);
                                }

                                enriched.add(
                                                allUrls.isEmpty()
                                                                ? result
                                                                : result.withImageUrls(allUrls));
                        }
                } catch (Exception e) {
                        throw new IOException("Failed to search image URLs", e);
                }

                return enriched;
        }

        // find box bounds for image previews
        private ArtworkBoxBounds runGeminiAndGetBoxBounds(Content content)
                        throws IOException {

                GenerateContentResponse response = client.models.generateContent(
                                "gemini-2.5-flash",
                                List.of(content),
                                GenerateContentConfig.builder().build());

                String json = response.text();

                ArtworkBoxBounds result = objectMapper.readValue(
                                json,
                                new TypeReference<ArtworkBoxBounds>() {
                                });

                return result;
        }

        // builders
        private Content buildImageContent(MultipartFile image) throws IOException {

                Blob blob = Blob.builder()
                                .mimeType(image.getContentType())
                                .data(image.getBytes())
                                .build();

                return Content.builder()
                                .parts(List.of(
                                                Part.builder().inlineData(blob).build(),
                                                Part.builder().text(IMAGE_PROMPT).build()))
                                .build();
        }

        private Content buildBytesImageContent(
                        InputStream inputStream,
                        String mimeType) throws IOException {

                byte[] bytes = inputStream.readAllBytes();

                Blob blob = Blob.builder()
                                .mimeType(mimeType)
                                .data(bytes)
                                .build();

                return Content.builder()
                                .parts(List.of(
                                                Part.builder().inlineData(blob).build(),
                                                Part.builder().text(BOX_DETECTION_PROMPT).build()))
                                .build();
        }

        private Content buildKeywordContent(String keywords) {

                return Content.builder()
                                .parts(List.of(
                                                Part.builder().text(KEYWORD_PROMPT).build(),
                                                Part.builder().text(keywords).build()))
                                .build();
        }

        // prompts
        private static final String IMAGE_PROMPT = """
                        You are an expert in art history.

                        Task:
                        Analyze the image and identify ALL real, catalogued art subjects that apply.

                        A valid subject is one that:
                        - Exists as a named work, structure, or photograph
                        - Is documented or referenceable in at least one of:
                           museums, archives, architectural records, wikipedia as an artwork,
                          scholarly sources, exhibition catalogues, or historical documentation.

                        Recognize this artwork and propose 2-3 possible options.
                        Describe the artist or architect, title, year or period, and art movement.

                        Generate an original 150–180 word description as an art historian.
                        Do not copy text from Wikipedia or museum sites verbatim.

                        Respond ONLY with valid JSON:

                        [
                          {
                            "title": "string",
                            "artist": "string",
                            "year": "string",
                            "movement": "string",
                            "description": "string",
                            "keywords": "string",
                          }
                        ]

                        Rules:
                        - If unknown, use "Unknown"
                        - Keywords: up to 3 distinctive words including artist and title, no art movement, no commas, no filler words (e.g. gogh starry mona lisa rembrandt repin)
                        - Do NOT include explanations
                        - Do NOT reinterpret casual photos as artworks
                        - If a real building or structure is shown, return it

                        Return ONLY raw JSON.
                        Do NOT use markdown.
                        """;

        private static final String KEYWORD_PROMPT = """
                        You are an expert in art history.

                        Task:
                        Based on keywords, identify ALL real, catalogued artworks that apply.

                        A valid subject:
                        - Exists as a named artwork, structure, or photograph as art
                        - Is documented in reliable sources (museums, galleries, wikipedia)

                        Propose 2–3 possible options.
                        Describe the artist or architect, title, year or period, and movement.

                        Generate an original 150–180 word description.
                        Do not copy text verbatim from external sources.

                        Respond ONLY with valid JSON:

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
                        - If unknown, use "Unknown"
                        - Do NOT include explanations

                        Keyword rules:
                        - EXACTLY three lowercase words
                        - words must be space-delimited (never concatenate words)
                        - each word must be a real standalone word
                        - must uniquely identify both the artist and the artwork
                        - optimized for Wikimedia Commons / museum image search
                        - prioritize the most distinctive title words
                        - include the artist’s last name
                        - do NOT include art movement
                        - do NOT use filler words unless they are required for disambiguation
                        - do NOT merge or compress multiple title words into one token

                        Return ONLY raw JSON.
                        Do NOT use markdown.
                        """;

        private static final String BOX_DETECTION_PROMPT = """
                        Analyze the image and return exactly ONE bounding box.

                        Step 1 — Determine if the image is a portrait.

                        Treat the image as a portrait ONLY if ALL of the following are true:
                        - A single, dominant human face is visible
                        - The face is the primary subject of the image
                        - The face occupies a large portion of the image
                        - The image clearly depicts one individual or a focused group

                        Do NOT treat paintings, historical scenes, narrative artworks,
                        or multi-figure compositions as portraits, even if faces are visible.

                        Step 2 — Select the bounding box.

                        Case A — Portrait image:
                        - Identify detected faces
                        - Select the most prominent face
                        - Expand the bounding box uniformly as much as the image allows
                        - Keep the face centered in the box
                        - Ensure the box remains fully within the image boundaries

                        Case B — Not a portrait:
                        - Return a large bounding box centered on the image focusing on the center
                        - Preserve most or all of the image content
                        - Prefer minimal or no cropping

                        Step 3 — Output format.

                        Return ONLY valid JSON in the following format:

                        {
                          "x": number,
                          "y": number,
                          "width": number,
                          "height": number
                        }

                        Rules:
                        - Coordinates must be normalized between 0 and 1
                        - Do NOT include explanations
                        - Do NOT include markdown
                        - Do NOT return multiple boxes
                        - Do NOT wrap the output in code blocks
                        """;

}
