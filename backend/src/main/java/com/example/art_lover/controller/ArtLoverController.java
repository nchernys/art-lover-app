package com.example.art_lover.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.art_lover.dto.artwork.*;
import com.example.art_lover.model.ArtistModel;
import com.example.art_lover.service.ArtworkService;
import com.example.art_lover.service.GeminiAIDescriptionService;
import com.example.art_lover.service.GeminiAIImageRecognitionService;

@RestController
@RequestMapping("/api")
public class ArtLoverController {

	private final ArtworkService artworkService;
	private final GeminiAIDescriptionService descriptionService;
	private final GeminiAIImageRecognitionService imageRecognitionService;

	public ArtLoverController(
			ArtworkService artworkService,
			GeminiAIDescriptionService descriptionService,
			GeminiAIImageRecognitionService imageRecognitionService) {
		this.artworkService = artworkService;
		this.descriptionService = descriptionService;
		this.imageRecognitionService = imageRecognitionService;
	}

	// artwork CRUD

	@PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ArtworkResponse> saveArtwork(
			@ModelAttribute CreateArtworkCommand artwork,
			@RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
			Authentication authentication) {

		artworkService.saveArtwork(artwork, imageFile, authentication.getName());
		return ResponseEntity.ok(new ArtworkResponse("Artwork saved successfully"));
	}

	@GetMapping("/show/{id}")
	public ArtworkDetailsView getArtwork(@PathVariable String id) {
		return artworkService.showOne(id);
	}

	@GetMapping("/show")
	public List<ArtworkDetailsView> getUserArtworks(Authentication authentication) {
		return artworkService.showAllByUserId(authentication.getName());
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteArtwork(
			@PathVariable String id,
			Authentication authentication) {

		artworkService.deleteArtwork(id, authentication.getName());
		return ResponseEntity.ok("Artwork deleted");
	}

	@PatchMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> updateArtwork(
			@PathVariable String id,
			@ModelAttribute UpdateArtworkCommand artwork,
			@RequestPart(required = false) MultipartFile image,
			Authentication authentication) {

		artworkService.updateArtwork(id, artwork, image, authentication.getName());
		return ResponseEntity.ok("Artwork updated");
	}

	@PatchMapping("/update/bookmark/{id}")
	public ResponseEntity<String> updateBookmark(
			@PathVariable String id,
			@RequestParam boolean bookmark,
			Authentication authentication) {

		artworkService.updateArtworkBookmark(id, bookmark, authentication.getName());
		return ResponseEntity.ok("Bookmark updated");
	}

	// AI features (generate artwork description, recognize artwork from image)

	@GetMapping("/generate-description")
	public ResponseEntity<String> generateDescription(@RequestParam String userPrompt) {
		return ResponseEntity.ok(descriptionService.generateDescription(userPrompt));
	}

	@PostMapping(value = "/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<ArtworkSearchHit> recognizeFromImage(
			@RequestParam("image") MultipartFile image) {

		return imageRecognitionService.recognizeImage(image);
	}

	@PostMapping("/recognize-keywords")
	public List<ArtworkSearchHit> recognizeFromKeywords(
			@RequestParam String keywords) {

		return imageRecognitionService.recognizeKeywords(keywords);
	}

	// artists (fetch all)

	@GetMapping("/artists")
	public List<ArtistModel> getArtists() {
		return artworkService.findAllArtists();
	}
}
