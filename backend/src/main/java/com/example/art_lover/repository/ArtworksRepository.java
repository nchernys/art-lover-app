package com.example.art_lover.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
// to connect the spring boot app to mongo 
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.art_lover.model.ArtworkModel;

// interface 
public interface ArtworksRepository
        extends MongoRepository<ArtworkModel, String> {

    // add full-text search index to the db and search through indexed fields
    @Query("{ $text: { $search: ?0 } }")
    List<ArtworkModel> searchByKeyword(String keyword);

    // search artworks by user id
    List<ArtworkModel> findByUserId(String userId);

}
