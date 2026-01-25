package com.example.art_lover.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.art_lover.model.ArtworkModel;
import com.example.art_lover.model.ArtistModel;
import com.example.art_lover.repository.ArtworksRepository;
import com.example.art_lover.repository.ArtistRepository;
import com.example.art_lover.dto.artwork.ArtworkGalleryDisplay;
import com.example.art_lover.dto.artwork.ArtworkGallerySave;
import com.example.art_lover.dto.artwork.ArtworkSearchResult;
import com.example.art_lover.dto.r2.R2ImageUploadResponse;
import com.example.art_lover.exceptions.ForbiddenOperationException;
import com.example.art_lover.exceptions.ResourceNotFoundException;

@Service
public class ArtworkService {

    private final ArtworksRepository artworkRepository;
    private final ArtistRepository artistRepository;
    private final R2ImageService r2ImageService;

    public ArtworkService(ArtworksRepository artworkRepository, ArtistRepository artistRepository,
            R2ImageService r2ImageService) {
        this.artworkRepository = artworkRepository;
        this.artistRepository = artistRepository;
        this.r2ImageService = r2ImageService;
    }

    public ArtworkGalleryDisplay showOne(String id) {
        // fetch one artwork record from the db with artist name included
        ArtworkGalleryDisplay artwork = artworkRepository.findArtworkWithArtist(id);
        return artwork;
    }

    public List<ArtworkGalleryDisplay> showAllByUserId(String userId) {
        // fetch all artwork records from the db
        // for the currently logged in user
        // & with the artist name
        List<ArtworkGalleryDisplay> artworks = artworkRepository.findArtworkWithArtistByUserId(userId);
        return artworks;
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

    public void saveArtwork(ArtworkGallerySave dto, MultipartFile imageFile, String userId) {
        ArtworkModel artwork = new ArtworkModel();
        if (imageFile != null && !imageFile.isEmpty()) {
            // save the image to Cloudflare R2 storage and get the image url
            R2ImageUploadResponse data = r2ImageService.uploadImage(imageFile);
            // save the image url to the artwork object
            artwork.setImageUrl(data.getUrl());
            artwork.setImageKey(data.getKey());

        } else {
            artwork.setImageUrl(dto.imageUrl());
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
        // if the artwork object has a valid image key, delete the image from R2 storage
        // & delete the artwork record from the db
        if (artwork.getImageKey() != null) {
            r2ImageService.deleteImage(artwork.getImageKey());
        }

        // find all artworks by this artist in the db (all users)
        long count = artworkRepository.countByArtistId(artwork.getArtistId());

        // if the number of artworks by this artist in the db (all users) is 1, can
        // delete the artist safely
        if (count == 1) {
            artistRepository.deleteById(artwork.getArtistId());
        }
        artworkRepository.deleteById(id);
    }

    public void updateArtwork(String id, ArtworkModel artwork, MultipartFile image, String userId) {
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
        if (image != null && !image.isEmpty()) {

            // delete old image from R2
            if (toUpdate.getImageKey() != null) {
                r2ImageService.deleteImage(toUpdate.getImageKey());
            }

            R2ImageUploadResponse data = r2ImageService.uploadImage(image);

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

    public List<ArtworkSearchResult> searchArt(String keyword) {
        List<ArtworkSearchResult> localResults = artworkRepository.searchByKeyword(keyword)
                .stream()
                .map(a -> new ArtworkSearchResult(
                        a.getTitle(),
                        a.getArtistId(),
                        String.valueOf(a.getYear()),
                        a.getMovement(),
                        a.getImageKey() == null
                                ? List.of()
                                : List.of(a.getImageKey()),
                        String.valueOf(a.getTitle()),
                        a.getDescription()))
                .toList();

        return localResults.stream().toList();
    }

    public List<ArtistModel> findAllArtists() {
        // fetch all artists from the db (all users) and sort in ascending order
        List<ArtistModel> artists = artistRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        return artists;
    }

}
