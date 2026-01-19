package com.example.art_lover.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.art_lover.model.ArtworkModel;
import com.example.art_lover.repository.ArtworksRepository;
import com.example.art_lover.dto.artwork.ArtworkSearchResult;
import com.example.art_lover.dto.r2.R2ImageUploadResponse;
import com.example.art_lover.exceptions.ForbiddenOperationException;

import org.springframework.security.core.Authentication;

@Service
public class ArtworkService {

    private final ArtworksRepository repository;
    private final R2ImageService r2ImageService;

    public ArtworkService(ArtworksRepository respository, R2ImageService r2ImageService) {
        this.repository = respository;
        this.r2ImageService = r2ImageService;
    }

    public ArtworkModel showOne(String id) {
        // fetch one artwork record from the db
        // & throw an error if not found
        ArtworkModel artwork = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));
        return artwork;
    }

    public List<ArtworkModel> showAllByUserId(String userId) {
        // fetch all artwork records from the db for the currently logged in user
        List<ArtworkModel> artworks = repository.findByUserId(userId);
        return artworks;
    }

    public void saveArtwork(ArtworkModel artwork, MultipartFile imageFile, String userId) {
        if (imageFile != null && !imageFile.isEmpty()) {
            // save the image to Cloudflare R2 storage and get the image url
            R2ImageUploadResponse data = r2ImageService.uploadImage(imageFile);
            // save the image url to the artwork object
            artwork.setImageUrl(data.getUrl());
            artwork.setImageKey(data.getKey());

        } else {
            artwork.setImageUrl(artwork.getImageUrl());
        }
        // save the artwork object with the image url to MongoDB
        artwork.setUserId(userId);
        repository.save(artwork);
    }

    public void deleteArtwork(String id, String userId) {
        // find the artwork record by id & throw an error if not found
        ArtworkModel artwork = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found."));
        if (!Objects.equals(artwork.getUserId(), userId))
            throw new ForbiddenOperationException("User is not authorized to delete this artwork.");
        // if the artwork object has a valid image key, delete the image from R2 storage
        // & delete the artwork record from the db
        if (artwork.getImageKey() != null) {
            r2ImageService.deleteImage(artwork.getImageKey());
        }
        repository.deleteById(id);
    }

    public void updateArtwork(String id, ArtworkModel artwork, MultipartFile image, String userId) {
        ArtworkModel toUpdate = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found."));
        if (!Objects.equals(artwork.getUserId(), userId))
            throw new ForbiddenOperationException("User is not authorized to update this artwork.");
        toUpdate.setTitle(artwork.getTitle());
        toUpdate.setArtist(artwork.getArtist());
        toUpdate.setYear(artwork.getYear());
        toUpdate.setContinent(artwork.getContinent());
        toUpdate.setCountry(artwork.getCountry());
        toUpdate.setBookmark(artwork.getBookmark());
        toUpdate.setMovement(artwork.getMovement());
        toUpdate.setDescription(artwork.getDescription());

        // only update image if new image is added
        if (image != null && !image.isEmpty()) {

            // delete old image from R2
            if (toUpdate.getImageKey() != null) {
                r2ImageService.deleteImage(toUpdate.getImageKey());
            }

            R2ImageUploadResponse data = r2ImageService.uploadImage(image);

            toUpdate.setImageKey(data.getKey());
            toUpdate.setImageUrl(data.getUrl());
        }

        repository.save(toUpdate);
    }

    public void updateArtworkBookmark(String id, Boolean bookmark, String userId) {
        ArtworkModel toUpdate = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found."));
        if (!Objects.equals(toUpdate.getUserId(), userId))
            throw new ForbiddenOperationException("User is not authorized to delete this artwork.");
        toUpdate.setBookmark(bookmark);
        repository.save(toUpdate);
    }

    public List<ArtworkSearchResult> searchArt(String keyword) {
        List<ArtworkSearchResult> localResults = repository.searchByKeyword(keyword)
                .stream()
                .map(a -> new ArtworkSearchResult(
                        a.getTitle(),
                        a.getArtist(),
                        String.valueOf(a.getYear()),
                        a.getMovement(),
                        a.getImageKey() == null
                                ? List.of()
                                : List.of(a.getImageKey()),
                        String.valueOf(a.getArtist() + " " + a.getTitle()),
                        a.getDescription()))
                .toList();

        return localResults.stream().toList();
    }

}
