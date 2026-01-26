package com.example.art_lover.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
// to connect the spring boot app to mongo 
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.art_lover.dto.artwork.ArtworkGalleryDisplay;
import com.example.art_lover.model.ArtworkModel;

// interface 
public interface ArtworksRepository
                extends MongoRepository<ArtworkModel, String> {

        // add full-text search index to the db and search through indexed fields
        @Query("{ $text: { $search: ?0 } }")
        List<ArtworkModel> searchByKeyword(String keyword);

        // search artworks by user id
        List<ArtworkModel> findByUserId(String userId);

        @Aggregation(pipeline = {
                        "{ $match: { _id: ?0 } }",
                        "{ $lookup: { from: 'artists', localField: 'artistId', foreignField: '_id', as: 'artistName' } }",
                        "{ $unwind: '$artist' }"
        })
        ArtworkGalleryDisplay findArtworkWithArtist(String artworkId);

        @Aggregation(pipeline = {
                        "{ $match: { userId: ?0 } }",
                        "{ $addFields: { artistObjId: { $toObjectId: '$artistId' } } }",
                        "{ $lookup: { from: 'artists', localField: 'artistObjId', foreignField: '_id', as: 'artist' } }",
                        "{ $unwind: '$artist' }",
                        "{ $project: { " +
                                        "id: '$_id', " +
                                        "title: 1, " +
                                        "artist: '$artist.name', " +
                                        "year: 1, " +
                                        "movement: 1, " +
                                        "imageUrl: 1, " +
                                        "imageKey: 1, " +
                                        "previewKey: 1, " +
                                        "description: 1, " +
                                        "bookmark: 1 " +
                                        "} }"
        })
        List<ArtworkGalleryDisplay> findArtworkWithArtistByUserId(String userId);

        List<ArtworkModel> findByArtistId(String artistId);

        long countByArtistId(String artistId);
}
