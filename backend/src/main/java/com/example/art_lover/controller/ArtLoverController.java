package com.example.art_lover.controller;

import java.util.List;
import java.util.Collections;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.art_lover.dto.artwork.ArtworkDetailsView;
import com.example.art_lover.dto.artwork.CreateArtworkCommand;
import com.example.art_lover.dto.artwork.ArtworkResponse;
import com.example.art_lover.dto.artwork.ArtworkSearchHit;
import com.example.art_lover.dto.security.AuthRequest;
import com.example.art_lover.dto.security.AuthResponse;
import com.example.art_lover.exceptions.EmailAlreadyExistsException;
import com.example.art_lover.model.ArtworkModel;
import com.example.art_lover.model.ArtistModel;
import com.example.art_lover.model.UserModel;
import com.example.art_lover.repository.UserRepository;
import com.example.art_lover.service.ArtworkService;
import com.example.art_lover.service.GeminiAIDescriptionService;
import com.example.art_lover.service.GeminiAIImageRecognitionService;
import com.example.art_lover.service.JWTService;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestPart;
import java.io.IOException;

@RestController
public class ArtLoverController {

	private final ArtworkService artworkService;
	private final GeminiAIDescriptionService aiDescriptionService;
	private final GeminiAIImageRecognitionService geminiAiImageRecognitionService;

	// constructor
	public ArtLoverController(ArtworkService artworkService, GeminiAIDescriptionService aiDescriptionService,
			GeminiAIImageRecognitionService aiImageRecognitionService) {
		this.artworkService = artworkService;
		this.aiDescriptionService = aiDescriptionService;
		this.geminiAiImageRecognitionService = aiImageRecognitionService;
	}

	@PostMapping(value = "/api/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ArtworkResponse> saveArtwork(
			@ModelAttribute CreateArtworkCommand artwork,
			@RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
			Authentication authentication) {

		String userId = authentication.getName();
		try {
			artworkService.saveArtwork(artwork, imageFile, userId);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ArtworkResponse("Failed to save artwork."));
		}

		return ResponseEntity.ok(
				new ArtworkResponse("Artwork saved successfully"));
	}

	@GetMapping("/api/show/{id}")
	public ArtworkDetailsView showOne(@PathVariable String id) {
		return artworkService.showOne(id);
	}

	@GetMapping("/api/show")
	public List<ArtworkDetailsView> show(Authentication authentication) {
		String userId = authentication.getName();
		return artworkService.showAllByUserId(userId);
	}

	@DeleteMapping("/api/delete/{id}")
	public ResponseEntity<String> deleteArt(@PathVariable String id, Authentication authentication) {
		String userId = authentication.getName();
		artworkService.deleteArtwork(id, userId);
		return ResponseEntity.ok("Artwork deleted.");
	}

	@PatchMapping("/api/update/{id}")
	public ResponseEntity<String> updateArt(@PathVariable String id, @ModelAttribute ArtworkModel artwork,
			@RequestPart MultipartFile image, Authentication authentication) {
		String userId = authentication.getName();
		try {
			artworkService.updateArtwork(id, artwork, image, userId);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to update artwork.");
		}
		return ResponseEntity.ok("Artwork updated.");

	}

	@PatchMapping("/api/update/bookmark/{id}")
	public ResponseEntity<String> updateBookmark(@PathVariable String id, @RequestParam boolean bookmark,
			Authentication authentication) {
		String userId = authentication.getName();
		artworkService.updateArtworkBookmark(id, bookmark, userId);
		return ResponseEntity.ok("Bookmark updated.");
	}

	@GetMapping("/api/generate-description")
	public String generateDescription(@RequestParam String userPrompt) {
		String description = aiDescriptionService.generateDescription(userPrompt);
		return description;
	}

	@PostMapping(value = "/api/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<ArtworkSearchHit> recognizeArtworkFromImage(@RequestParam("image") MultipartFile image) {
		try {
			List<ArtworkSearchHit> results = geminiAiImageRecognitionService.recognizeImage(image);
			return results;
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@PostMapping(value = "/api/recognize-keywords")
	public List<ArtworkSearchHit> recognizeArtworkFromKeywords(@RequestParam("keywords") String keywords) {
		try {
			List<ArtworkSearchHit> results = geminiAiImageRecognitionService.recognizeKeywords(keywords);
			return results;
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@GetMapping("/api/artists")
	public List<ArtistModel> getArtists() {
		List<ArtistModel> artists = artworkService.findAllArtists();
		return artists;
	}

	@RestController
	@RequestMapping("/api/auth")
	public class AuthController {

		private final UserRepository userRepository;
		private final PasswordEncoder passwordEncoder;
		private final JWTService jwtService;

		public AuthController(UserRepository userRepository,
				PasswordEncoder passwordEncoder,
				JWTService jwtService) {
			this.userRepository = userRepository;
			this.passwordEncoder = passwordEncoder;
			this.jwtService = jwtService;
		}

		@PostMapping("/signup")
		public ResponseEntity<Void> signup(@RequestBody AuthRequest req) {

			if (userRepository.findByEmail(req.getEmail()).isPresent()) {
				throw new EmailAlreadyExistsException("Account already exists");
			}

			UserModel user = new UserModel();
			user.setEmail(req.getEmail());
			user.setPassword(passwordEncoder.encode(req.getPassword()));

			userRepository.save(user);

			return ResponseEntity.ok().build();
		}

		@PostMapping("/login")
		public ResponseEntity<Void> login(@RequestBody AuthRequest req,
				HttpServletResponse response) {

			UserModel user = userRepository.findByEmail(req.getEmail())
					.orElseThrow(() -> new RuntimeException("Invalid credentials"));

			if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
				throw new RuntimeException("Invalid credentials");
			}

			String token = jwtService.generateToken(user.getId());

			ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", token)
					.httpOnly(true)
					.secure(true) // true in production
					.path("/")
					.sameSite("None") // Strict in production
					.maxAge(60 * 60) // 1 hour
					.build();

			response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

			return ResponseEntity.ok().build();
		}

		@PostMapping("/logout")
		public ResponseEntity<Void> logout(HttpServletResponse response) {

			ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", "")
					.httpOnly(true)
					.secure(false) // true in production
					.path("/")
					.maxAge(0)
					.build();

			response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

			return ResponseEntity.ok().build();
		}

		@GetMapping("/me")
		public ResponseEntity<String> me(Authentication authentication) {
			return ResponseEntity.ok(authentication.getName()); // id
		}

	}
}