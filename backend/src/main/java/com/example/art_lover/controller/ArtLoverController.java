package com.example.art_lover.controller;

import java.util.List;
import java.util.Collections;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.art_lover.dto.ArtworkResponse;
import com.example.art_lover.dto.ArtworkSearchResult;
import com.example.art_lover.model.ArtworkModel;
import com.example.art_lover.service.ArtworkService;
import com.example.art_lover.service.GeminiAIDescriptionService;
import com.example.art_lover.service.GeminiAILImageRecognitionService;
import org.springframework.web.bind.annotation.RequestPart;
import java.io.IOException;

@RestController
public class ArtLoverController {

	private final ArtworkService artworkService;
	private final GeminiAIDescriptionService aiDescriptionService;
	private final GeminiAILImageRecognitionService aiImageRecognitionService;

	// constructor
	public ArtLoverController(ArtworkService artworkService, GeminiAIDescriptionService aiDescriptionService,
			GeminiAILImageRecognitionService aiImageRecognitionService) {
		this.artworkService = artworkService;
		this.aiDescriptionService = aiDescriptionService;
		this.aiImageRecognitionService = aiImageRecognitionService;
	}

	@PostMapping(value = "/api/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ArtworkResponse> addArtwork(
			@ModelAttribute ArtworkModel artwork,
			@RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

		artworkService.saveArtwork(artwork, imageFile);

		return ResponseEntity.ok(
				new ArtworkResponse("Artwork saved successfully"));
	}

	@GetMapping("/api/show/{id}")
	public ArtworkModel showArt(@PathVariable String id) {
		return artworkService.showArt(id);
	}

	@GetMapping("/api/show")
	public List<ArtworkModel> showArtAll() {
		return artworkService.showArtAll();
	}

	@DeleteMapping("/api/delete/{id}")
	public ResponseEntity<String> deleteArt(@PathVariable String id) {
		artworkService.deleteArtwork(id);
		return ResponseEntity.ok("Artwork deleted.");
	}

	@PatchMapping("/api/update/{id}")
	public ResponseEntity<String> updateArt(@PathVariable String id, @ModelAttribute ArtworkModel artwork,
			@RequestPart MultipartFile image) {
		artworkService.updateArtwork(id, artwork, image);
		return ResponseEntity.ok("Artwork updated.");
	}

	@PatchMapping("/api/update/bookmark/{id}")
	public ResponseEntity<String> updateBookmark(@PathVariable String id, @RequestParam boolean bookmark) {
		artworkService.updateArtworkBookmark(id, bookmark);
		return ResponseEntity.ok("Bookmark updated.");
	}

	@GetMapping("/api/search")
	public List<ArtworkSearchResult> searchArt(@RequestParam String keyword) {
		List<ArtworkSearchResult> artworksFound = artworkService.searchArt(keyword);
		return artworksFound;
	}

	@GetMapping("/api/generate-description")
	public String generateDescription(@RequestParam String userPrompt) {
		String description = aiDescriptionService.generateDescription(userPrompt);
		return description;
	}

	@PostMapping(value = "/api/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<ArtworkSearchResult> recognizeArtworkFromImage(@RequestParam("image") MultipartFile image) {
		try {
			System.out.println("IMAGE " + image);
			List<ArtworkSearchResult> results = aiImageRecognitionService.recognizeImage(image);
			return results;
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

}
