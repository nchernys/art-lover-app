package com.example.art_lover.service;

import java.util.List;
import java.util.stream.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.art_lover.model.ArtworkModel;
import com.example.art_lover.repository.ArtworksRepository;
import com.example.art_lover.dto.ArtworkSearchResult;
import com.example.art_lover.dto.R2ImageUploadResponse;

@Service
public class ArtworkService {

    private final ArtworksRepository repository;
    private final R2ImageService r2ImageService;
    private final MetMuseumService metService;

    public ArtworkService(ArtworksRepository respository, R2ImageService r2ImageService, MetMuseumService metService) {
        this.repository = respository;
        this.r2ImageService = r2ImageService;
        this.metService = metService;
    }

    public ArtworkModel showArt(String id) {
        // fetch one artwork record from the db
        // & throw an error if not found
        ArtworkModel artwork = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));
        return artwork;
    }

    public List<ArtworkModel> showArtAll() {
        // fetch all artwork records from the db
        List<ArtworkModel> artworks = repository.findAll();
        return artworks;
    }

    public void saveArtwork(ArtworkModel artwork, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            // save the image to Cloudflare R2 storage and get the image url
            R2ImageUploadResponse data = r2ImageService.uploadImage(imageFile);
            // save the image url to the artwork object
            artwork.setImageUrl(data.getUrl());
            artwork.setImageKey(data.getKey());
            System.out.println("ARTWORK WITH KEY ___________ " + artwork.getImageKey());
        } else {
            artwork.setImageUrl(artwork.getImageUrl());
        }
        // save the artwork object with the image url to MongoDB
        repository.save(artwork);
    }

    public void deleteArtwork(String id) {
        // find the artwork record by id & throw an error if not found
        ArtworkModel artwork = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found."));
        // if the artwork object has a valid image key, delete the image from R2 storage
        // & delete the artwork record from the db
        if (artwork.getImageKey() != null) {
            r2ImageService.deleteImage(artwork.getImageKey());
        }
        repository.deleteById(id);
    }

    public void updateArtwork(String id, ArtworkModel artwork, MultipartFile image) {
        ArtworkModel toUpdate = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found."));

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

    public void updateArtworkBookmark(String id, Boolean bookmark) {
        ArtworkModel toUpdate = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found."));
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

        List<ArtworkSearchResult> metResults = metService.search(keyword);

        return Stream.concat(localResults.stream(), metResults.stream()).toList();
    }

}
