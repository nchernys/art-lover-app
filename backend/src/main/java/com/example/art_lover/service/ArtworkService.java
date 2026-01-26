package com.example.art_lover.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.art_lover.model.ArtworkModel;
import com.example.art_lover.model.ArtistModel;
import com.example.art_lover.repository.ArtworksRepository;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.example.art_lover.repository.ArtistRepository;
import com.example.art_lover.dto.artwork.ArtworkBoxBounds;
import com.example.art_lover.dto.artwork.ArtworkGalleryDisplay;
import com.example.art_lover.dto.artwork.ArtworkGallerySave;
import com.example.art_lover.dto.r2.R2ImageUploadResponse;
import com.example.art_lover.exceptions.ForbiddenOperationException;
import com.example.art_lover.exceptions.ResourceNotFoundException;

@Service
public class ArtworkService {

    private final ArtworksRepository artworkRepository;
    private final ArtistRepository artistRepository;
    private final R2ImageService r2ImageService;
    private final ObjectMapper objectMapper;
    private final GeminiAIImageRecognitionService geminiAIImageRecognitionService;

    public ArtworkService(ArtworksRepository artworkRepository, ArtistRepository artistRepository,
            R2ImageService r2ImageService, ObjectMapper objectMapper,
            GeminiAIImageRecognitionService geminiAIImageRecognitionService) {
        this.artworkRepository = artworkRepository;
        this.artistRepository = artistRepository;
        this.r2ImageService = r2ImageService;
        this.objectMapper = objectMapper;
        this.geminiAIImageRecognitionService = geminiAIImageRecognitionService;
    }

    // download image from public urls to create gallery previews
    private InputStream downloadImage(String imageUrl) throws IOException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .header(
                        "User-Agent",
                        "ArtLoverApp/1.0 (contact: ch@gmail.com)")
                .header("Accept", "image/*")
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                throw new IOException(
                        "Failed to download image: HTTP " + response.statusCode());
            }

            return new ByteArrayInputStream(response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Image download interrupted", e);
        }
    }

    // create gallery preview
    public byte[] createPreview(InputStream inputStream, ArtworkBoxBounds box)
            throws IOException {

        BufferedImage image = ImageIO.read(inputStream);

        int imgW = image.getWidth();
        int imgH = image.getHeight();

        // Convert normalized box → pixels
        double boxX = box.x() * imgW;
        double boxY = box.y() * imgH;
        double boxW = box.width() * imgW;
        double boxH = box.height() * imgH;

        // Center of detected box
        double centerX = boxX + boxW / 2.0;
        double centerY = boxY + boxH / 2.0;

        // Make square using the larger dimension
        double size = Math.max(boxW, boxH);

        // Expand to include head / hair
        size *= 1.8; // adjust if needed (1.2–1.5)

        // Bias upward slightly (faces need more space above eyes)
        // centerY -= boxH * 0.15;

        int half = (int) (size / 2);

        int cropX = (int) (centerX - half);
        int cropY = (int) (centerY - half);
        int cropSize = (int) size;

        // Clamp to image bounds
        if (cropX < 0)
            cropX = 0;
        if (cropY < 0)
            cropY = 0;

        if (cropX + cropSize > imgW) {
            cropSize = imgW - cropX;
        }
        if (cropY + cropSize > imgH) {
            cropSize = imgH - cropY;
        }

        BufferedImage cropped = image.getSubimage(cropX, cropY, cropSize, cropSize);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Thumbnails.of(cropped)
                .size(600, 600)
                .outputFormat("jpg")
                .outputQuality(1.0)
                .toOutputStream(out);

        return out.toByteArray();
    }

    private String getExtension(String fileName) {
        String extension = "";
        if (fileName != null) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex != -1) {
                extension = fileName.substring(dotIndex).toLowerCase();
            }
        }

        if (!extension.matches("\\.(png|jpg|jpeg|gif|webp|heic)")) {
            throw new IllegalArgumentException("Unsupported file type: " + extension);
        }

        return extension;
    }

    // resolve artist id
    private ArtistModel resolveArtist(ArtworkGallerySave dto) {

        // artistId provided
        if (dto.artistId() != null && !dto.artistId().isBlank()) {
            return artistRepository.findById(dto.artistId())
                    .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
        }

        // resolve id by name
        if (dto.artist() == null || dto.artist().isBlank()) {
            throw new IllegalArgumentException("Artist name is required");
        }

        return artistRepository.findByName(dto.artist())
                .orElseGet(() -> {
                    ArtistModel a = new ArtistModel();
                    a.setName(dto.artist());
                    return artistRepository.save(a);
                });
    }

    // fetch single gallery item
    public ArtworkGalleryDisplay showOne(String id) {
        // fetch one artwork record from the db with artist name included
        ArtworkGalleryDisplay artwork = artworkRepository.findArtworkWithArtist(id);
        return artwork;
    }

    // fetch all gallery items for the logged in user
    public List<ArtworkGalleryDisplay> showAllByUserId(String userId) {
        // fetch all artwork records from the db
        // for the currently logged in user
        // & with the artist name
        List<ArtworkGalleryDisplay> artworks = artworkRepository.findArtworkWithArtistByUserId(userId);
        return artworks;
    }

    public void saveArtwork(ArtworkGallerySave dto, MultipartFile imageFile, String userId) throws IOException {
        ArtworkModel artwork = new ArtworkModel();
        if (imageFile != null && !imageFile.isEmpty()) {
            String extension = getExtension(imageFile.getOriginalFilename());
            String key = "images/" + UUID.randomUUID() + extension;
            // save the image to Cloudflare R2 storage and get the image url
            R2ImageUploadResponse image = r2ImageService.uploadImage(
                    imageFile.getInputStream(),
                    imageFile.getSize(),
                    key,
                    imageFile.getContentType());

            // save the image url to the artwork object
            artwork.setImageUrl(image.getUrl());
            artwork.setImageKey(image.getKey());

        } else {
            // if image was not uploaded, save its public url
            artwork.setImageUrl(dto.imageUrl());
        }

        // crop and save face-weighted image preview
        byte[] imageBytes;

        String mimeType;

        if (imageFile != null && !imageFile.isEmpty()) {
            imageBytes = imageFile.getBytes();
            mimeType = imageFile.getContentType();
        } else {
            try (InputStream in = downloadImage(dto.imageUrl())) {
                imageBytes = in.readAllBytes();
            }
            mimeType = imageFile != null
                    ? imageFile.getContentType()
                    : "image/jpeg";
        }

        try (
                InputStream previewBytesForBoxDetection = new ByteArrayInputStream(imageBytes);
                InputStream previewBytesForPreviewCreation = new ByteArrayInputStream(imageBytes)) {
            ArtworkBoxBounds box = geminiAIImageRecognitionService
                    .identifyBoxBounds(previewBytesForBoxDetection, mimeType);

            String key = "previews/" + UUID.randomUUID() + ".jpg";
            byte[] preview = createPreview(previewBytesForPreviewCreation, box);

            if (preview.length == 0) {
                throw new IllegalStateException("Generated preview is empty");
            }

            R2ImageUploadResponse previewImg = r2ImageService.uploadImage(new ByteArrayInputStream(preview),
                    preview.length,
                    key,
                    "image/jpeg");

            artwork.setPreviewKey(previewImg.getKey());
        }

        // resolve artist name
        ArtistModel artist = resolveArtist(dto);
        artwork.setArtistId(artist.getId());

        // copy data from dto to artwork
        artwork.setTitle(dto.title());
        artwork.setYear(dto.year());
        artwork.setMovement(dto.movement());
        artwork.setDescription(dto.description());
        artwork.setUserId(userId);

        // save the artwork object with the image url to MongoDB
        artworkRepository.save(artwork);
    }

    public void deleteArtwork(String id, String userId) {
        // find the artwork record by id & throw an error if not found
        ArtworkModel artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found."));
        if (!Objects.equals(artwork.getUserId(), userId))
            throw new ForbiddenOperationException("User is not authorized to delete this artwork.");
        // if artwork has image key, delete the image from R2 storage
        if (artwork.getImageKey() != null) {
            r2ImageService.deleteImage(artwork.getImageKey());
        }
        // if artwork has preview key, delete the preview from R2 storage
        if (artwork.getPreviewKey() != null) {
            r2ImageService.deleteImage(artwork.getPreviewKey());
        }
        // find all artworks by this artist in the db (all users)
        long count = artworkRepository.countByArtistId(artwork.getArtistId());

        // if the number of artworks by this artist in the db (all users) is 1, can
        // delete the artist safely
        if (count == 1) {
            artistRepository.deleteById(artwork.getArtistId());
        }
        // delete the artwork from the db
        artworkRepository.deleteById(id);
    }

    public void updateArtwork(String id, ArtworkModel artwork, MultipartFile imageFile, String userId)
            throws IOException {
        ArtworkModel toUpdate = artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found."));
        if (!Objects.equals(artwork.getUserId(), userId))
            throw new ForbiddenOperationException("User is not authorized to update this artwork.");
        toUpdate.setTitle(artwork.getTitle());
        toUpdate.setArtistId(artwork.getArtistId());
        toUpdate.setYear(artwork.getYear());
        toUpdate.setContinent(artwork.getContinent());
        toUpdate.setCountry(artwork.getCountry());
        toUpdate.setBookmark(artwork.getBookmark());
        toUpdate.setMovement(artwork.getMovement());
        toUpdate.setDescription(artwork.getDescription());

        // only update image if new image is added
        if (imageFile != null && !imageFile.isEmpty()) {

            // delete old image from R2
            if (toUpdate.getImageKey() != null) {
                r2ImageService.deleteImage(toUpdate.getImageKey());
            }

            R2ImageUploadResponse data = r2ImageService.uploadImage(
                    imageFile.getInputStream(),
                    imageFile.getSize(),
                    imageFile.getOriginalFilename(),
                    imageFile.getContentType());

            toUpdate.setImageKey(data.getKey());
            toUpdate.setImageUrl(data.getUrl());
        }

        artworkRepository.save(toUpdate);
    }

    public void updateArtworkBookmark(String id, Boolean bookmark, String userId) {
        ArtworkModel toUpdate = artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found."));
        if (!Objects.equals(toUpdate.getUserId(), userId))
            throw new ForbiddenOperationException("User is not authorized to delete this artwork.");
        toUpdate.setBookmark(bookmark);
        artworkRepository.save(toUpdate);
    }

    public List<ArtistModel> findAllArtists() {
        // fetch all artists from the db (all users) and sort in ascending order
        List<ArtistModel> artists = artistRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        return artists;
    }

}
