package com.example.art_lover.repository;

import java.util.Optional;

// to connect the spring boot app to mongo 
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.art_lover.model.ArtistModel;

// interface 
public interface ArtistRepository
        extends MongoRepository<ArtistModel, String> {

    Optional<ArtistModel> findByName(String name);

}
